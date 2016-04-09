package com.example

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.example.KvStoreActor._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

/**
  * Created by jerryk on 4/4/16.
  */
class KvStoreActorSpec(_system: ActorSystem) extends TestKit(_system)
                                             with ImplicitSender
                                             with WordSpecLike
                                             with Matchers
                                             with BeforeAndAfterAll {

  // Auxiliary constructor that creates an actor system for our use
  def this() = this(ActorSystem("KvStoreActorSpec"))

  // Nicely shut down the ActorSystem that we create in the auxiliary
  // constructor once all of our tests have run.
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  ///////////////////////////////////////////////////////////////////////////
  //
  //                    T E S T    C A S E S  . . .
  //
  ///////////////////////////////////////////////////////////////////////////

  "A KvStore actor" must {
    "start life empty and report its size as zero" in {
      // NOTE: We use the Spec test's ActorSystem to create an Actor to test.
      //       Recall that all Actors are created by other Actors.  So when
      //       we create using actorOf on an ActorSystem, what happens?
      //       Looking at the path associated with the ActorRef we created
      //       we see that it ends up underneath the so called "user guardian
      //       actor".  We will learn more about guardians later when we study
      //       actor hierarchies and supervision.
      val kvStoreActor = system.actorOf(KvStoreActor.props, "heed-this!")
      println(s"The path of the created Actor is ${kvStoreActor.path}")
      // NOTE: See the '/user/' in the ActorRef's path above?

      // Now send a message to the actor under test...
      kvStoreActor ! GetSize    // NOTE: The send here comes from testActor,
                                // which is created for us by TestKit.

      // NOTE: Because we mixed in the ImplicitSender trait, replies to messages
      // we send in a test will end up in a queue upon which we can make
      // assertions...
      expectMsg(Size(0))
    }
  }

  // NOTE: The other test cases below should be enough for you to confirm
  //       that your implementation of the homework is doing the right things.
  //       Feel free to add tests, or read the documentation on TestKit, if
  //       you're curious.  We will revisit the subject of Actor testing
  //       throughout the course.

  "An empty KvStore actor" must {
    "grow to size 1 when a mapping is added" in {
      val kvStoreActor = system.actorOf(KvStoreActor.props)
      kvStoreActor ! Put("fooKey", "fooVal")
      kvStoreActor ! GetSize
      expectMsg(Size(1))
    }
  }

  "A KvStore actor" must {
    val kvStoreActor = system.actorOf(KvStoreActor.props)

    "not return values for a missing key" in {
      kvStoreActor ! Get("fooKey")
      expectMsg(Value(None))      // NOTE: The use of Option where a value may
                                  // not exist. Option and its relatives will
                                  // come up a lot later in the course.
    }
    "return a value previously put into the store" in {
      kvStoreActor ! Put("foo", "bar")
      kvStoreActor ! Get("foo")
      expectMsg(Value(Some("bar")))
    }
    "correctly remove a mapping present in the store" in {
      kvStoreActor ! Delete("foo")
      kvStoreActor ! Get("foo")
      expectMsg(Value(None))
    }
    "gracefully handle attempts to delete a key absent from the store" in {
      kvStoreActor ! Delete("baz")
      expectNoMsg
    }
  }

  "A KvStore actor" must {
    val kvStoreActor = system.actorOf(KvStoreActor.props)
    "have an empty key set when it's empty" in {
      kvStoreActor ! GetKeys
      expectMsg(Keys(Set.empty))
    }
  }
}
