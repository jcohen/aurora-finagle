package org.apache.aurora.tutorial.finagle

import java.nio.charset.StandardCharsets

import com.twitter.app.{App => TwitterApp}
import com.twitter.finagle.{Service, Http}
import com.twitter.util.{Await, Future}
import org.jboss.netty.buffer.ChannelBuffers._
import org.jboss.netty.handler.codec.http._

import scala.util.parsing.json.JSON

object Client extends TwitterApp {
  val port = flag("http_port", "8081", "The port to listen on.")
  val zkEnsemble = flag("zkEnsemble", "", "ZooKeeper ensemble for serverset resolution")

  val client = Http.newService(
    "/$/com.twitter.serverset/" + zkEnsemble() + "/aurora/vagrant/test/finagle-server")

  val service = new Service[HttpRequest, HttpResponse] {
    def apply(request: HttpRequest) = {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/")
      client(request) map { resp: HttpResponse =>
        val jsonString = resp.getContent.toString(StandardCharsets.UTF_8)
        val parsed: Map[String, String] = JSON.parseFull(jsonString) match {
          case Some(x) => x.asInstanceOf[Map[String, String]]
          case None => Map()
        }

        val response =
          new DefaultHttpResponse(request.getProtocolVersion, HttpResponseStatus.OK)
        response.setContent(
          copiedBuffer(
              s"Got word from ${parsed.get("host").get} that the current time is "
                  + s"${parsed.get("date").get}\n",
              StandardCharsets.UTF_8))

        response
      }
    }
  }

  def main() {
    val server = Http.serve("0.0.0.0:" + port(), service)
    println("Listening on " + port())
    onExit {
      server.close()
    }
    Await.ready(server)
  }
}
