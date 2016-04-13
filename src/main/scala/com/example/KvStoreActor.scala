package com.example

import akka.actor.{Actor, ActorLogging, Props}

import scala.collection.immutable.HashMap

/**
  * An Actor that implements a crude key-value store backed by a mutable map.
  */
object KvStoreActor {
  // It's a good practice to put the following here as a convenience for
  // getting the Props of this Actor so that we can easily create them.
  val props = Props[KvStoreActor]

  // Messages for the KvStore protocol
  case object GetSize           // Ask the store how many mappings it holds
  case class Size(size: Int)    // Reply to size query

  case class Put(key: String, value: String)  // Create a new mapping
  case class Get(key: String)                 // Look up a mapping
  case class Value(key: Option[String])       // Reply to look-up (note Option!)

  case class Delete(key: String)              // Remove mapping (fire & forget)

  case object GetKeys                         // Request the key set for the map
  case class Keys(keys: Set[String])          // Return the key set for the map
}


class KvStoreActor extends Actor with ActorLogging {
  import KvStoreActor._

  // TODO 1:Create a scala.collection.immutable.HashMap and capture it in a var.
  //        We favorable having a var that refers to an immutable map so that
  //        in the event we ever publish or share the map beyond the context of
  //        this actor, we know it won't be mutated in some other execution
  //        context.  This "var reference to an immutable data structure"
  //        pattern should be kept in mind for this reason: immutable objects
  //        are by construction thread safe, and if we can only change the
  //        var referring to such an object from one context (i.e. in our
  //        Actor), we derive safety benefits.
  var kvMap  = new scala.collection.immutable.HashMap[String, String]

  // TODO 2:Implement the receive method for the KvStoreActor.
  //        The KvStoreActor must respond to the messages below, as specified.
  //
  //         GetSize: return a Size message carrying the size of the map.
  //
  //         Put: update the map; no reply is sent to sender.
  //
  //         Get: look up key in map; return an Option of the value, if
  //              any.  For newcomers to Scala, be mindful of using the
  //              Option type in cases where a value may or may not exist.
  //              This pattern occurs in many of the standard libraries
  //              and we will want to bare it in mind when we encounter
  //              Try and Future later.
  //
  //         Delete: Remove a mapping from the map; no reply to sender.
  //
  //         GetKeys: Return a set of the keys in the map.
  //
  override def receive: Receive = {
    case GetSize =>
      log.info("Message GetSize received: Returning kvMap size using underlying size method of HashMap")
      sender() ! Size(kvMap.size)    // Reply to size query

    case Put(key,value) =>
      log.info("Message Put received: Updating internal key-value store in HashMap")
      kvMap += (key -> value)

    case Get(key) =>
      log.info("Message Get received: Returning Option-wrapped value from HashMap, using passed-in Key")
      var valueOption = Option(kvMap.get(key))
      sender() ! valueOption

    case Delete(key: String) =>
      log.info("Message Delete received: Returning Option-wrapped value from HashMap, using passed-in Key")
      kvMap -= key

    case GetKeys =>
      log.info("Message GetKeys received: Returning Set of key values")
      sender() ! Keys(kvMap.keySet)

  }
}
