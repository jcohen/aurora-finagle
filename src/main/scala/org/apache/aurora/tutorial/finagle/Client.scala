package org.apache.aurora.tutorial.finagle

import java.nio.charset.StandardCharsets

import com.twitter.app.{App => TwitterApp}
import com.twitter.finagle.Http
import com.twitter.util.Await
import org.jboss.netty.handler.codec.http._

object Client extends TwitterApp {
  val zkEnsemble = flag("zkEnsemble", "", "ZooKeeper ensemble for serverset resolution")

  def main () {
    val client = Http.newService(
      "/$/com.twitter.serverset/" + zkEnsemble() + "/aurora/vagrant/test/finagle-server")
    val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/")
    val response = client(request) onSuccess { resp: HttpResponse =>
      println("Got response from service: " + resp.getContent.toString(StandardCharsets.UTF_8))
    }
    Await.ready(response)
  }
}
