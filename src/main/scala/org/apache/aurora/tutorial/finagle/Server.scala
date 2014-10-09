package org.apache.aurora.tutorial.finagle

import java.net.InetAddress
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date

import com.twitter.app.{App => TwitterApp}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.{Await, Future}
import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import org.jboss.netty.handler.codec.http._

object Server extends TwitterApp {
  val port = flag("http_port", "8080", "The port to listen on.")
  val hostName = InetAddress.getLocalHost.getCanonicalHostName

  val service = new Service[HttpRequest, HttpResponse] {
    val sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z")

    def apply(request: HttpRequest) = {
      val response = new DefaultHttpResponse(request.getProtocolVersion, HttpResponseStatus.OK)
      response.setContent(
        copiedBuffer(
          s"""{"date": "${sdf.format(new Date())}", "host":"$hostName:${port()}"}""",
          StandardCharsets.UTF_8))
      Future.value(response)
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
