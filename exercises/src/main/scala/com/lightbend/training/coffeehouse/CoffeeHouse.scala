package com.lightbend.training.coffeehouse

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.lightbend.training.coffeehouse.CoffeeHouse.CreateGuest

import scala.concurrent.duration._

object CoffeeHouse {
  case class CreateGuest(favoriteCoffee: Coffee)

  def props(): Props = Props(new CoffeeHouse)
}

class CoffeeHouse extends Actor with ActorLogging {

  log.debug("CoffeeHouse Open")

  private val finishCoffeeDuration: FiniteDuration =
    context.system.settings.config.getDuration("coffee-house.guest.finish-coffee-duration", TimeUnit.MILLISECONDS).millis
  private val prepareCoffeeDuration: FiniteDuration =
    context.system.settings.config.getDuration("coffee-house.barista.prepare-coffee-duration", TimeUnit.MILLISECONDS).millis


  private val barista: ActorRef = createBarista()
  private val waiter: ActorRef = createWaiter()

  protected def createBarista(): ActorRef = {
    context.actorOf(Barista.props(prepareCoffeeDuration), "barista")
  }

  protected def createGuest(coffee: Coffee): ActorRef = {
    context.actorOf(Guest.props(waiter, coffee, finishCoffeeDuration  ))
  }
  protected def createWaiter(): ActorRef = context.actorOf(Waiter.props(barista), "waiter")

  override def receive: Receive = {
    case CreateGuest(favoriteCoffee) => createGuest(favoriteCoffee)
  }
}
