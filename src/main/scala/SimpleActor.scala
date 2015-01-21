package studyone

import akka.actor._
import akka.pattern.gracefulStop
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

class SimpleActor extends Actor with ActorLogging {

//  import context.dispatcher
  implicit val executionContext = context.system.dispatcher

//  context.watch(self) // <-- this is the only call needed for registration


  def receive = {
    case s: String =>
      log.info(s"$s | before future")
      Future {
        log.info(s"$s | in future before sleep")
        Thread.sleep(5000)
        log.info(s"$s | in future after sleep")
      }
      log.info(s"$s | after future")

    case m =>
      log.info(s"$m | unknown")
  }

}

object SimpleActorApp extends App {
  val system = ActorSystem("test")

  val actor = system.actorOf(Props(new SimpleActor), "simpleActor")

 //  val actor = system.actorOf(
//    Props(new SimpleActor).withDispatcher("my-dispatcher"),
//    "simpleActor")

  (1 to 10).foreach { n =>
    actor ! s"t$n"
    Thread.sleep(10)
  }


  val futures = gracefulStop(actor, 10 seconds)
  Await.result(futures, 10 seconds)
  system.shutdown()

//  Future.sequence(futures).onComplete(_ => system.shutdown)

// system.awaitTermination() not working
  //  try {
//    val stopped: Future[Boolean] = gracefulStop(actor, 5 seconds, Manager.Shutdown)
//    Await.result(stopped, 6 seconds)
//    // the actor has been stopped
//  } catch {
//    // the actor wasn't stopped within 5 seconds
//    case e: akka.pattern.AskTimeoutException =>
//  }

  // Shut down
//  actor ! PoisonPill
//  system.shutdown()


}
