package com.uangel.svc.biz.actor

import akka.actor.{AbstractActor, Stash, Timers}

abstract class AbstractActorWithStashWithTimer extends AbstractActor with Stash with Timers {

}
