package com.lightbend.training.coffeehouse

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import com.lightbend.training.coffeehouse.Guest.CoffeeFinished

import scala.concurrent.duration.FiniteDuration

object Guest {
  case object CoffeeFinished

  def props(waiter: ActorRef, favoriteCoffee: Coffee, finishCoffeeDuration: FiniteDuration): Props = {
    Props(new Guest(waiter, favoriteCoffee, finishCoffeeDuration))
  }
}

class Guest(waiter: ActorRef, favoriteCoffee: Coffee, finishCoffeeDuration: FiniteDuration)
  extends Actor with ActorLogging with Timers {
  private var coffeeCount: Int = 0

  orderCoffee()

  override def receive: Receive = {
    case Waiter.CoffeeServed(coffee) =>
      coffeeCount += 1
      log.info(s"Enjoying my $coffeeCount yummy $coffee")
      timers.startSingleTimer("coffee-finished", CoffeeFinished, finishCoffeeDuration)
    case CoffeeFinished =>
      orderCoffee()
  }

  private def orderCoffee() = {
    waiter ! Waiter.ServeCoffee(favoriteCoffee)
  }

  override def postStop(): Unit = {
    log.info("Goodbye")
    super.postStop()
  }
}
