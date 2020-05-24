package com.lightbend.training.coffeehouse

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.lightbend.training.coffeehouse.CoffeeHouse.CreateGuest

object CoffeeHouse {
  case class CreateGuest(favoriteCoffee: Coffee)

  def props(): Props = Props(new CoffeeHouse)
}

class CoffeeHouse extends Actor with ActorLogging {

  log.debug("CoffeeHouse Open")

  private val waiter: ActorRef = createWaiter()

  protected def createGuest(coffee: Coffee): ActorRef = {
    context.actorOf(Guest.props(waiter, coffee))
  }
  protected def createWaiter(): ActorRef = context.actorOf(Waiter.props(), "waiter")

  override def receive: Receive = {
    case CreateGuest(favoriteCoffee) => createGuest(favoriteCoffee)
  }
}
