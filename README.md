This description is for installing NDB on Macos , assuming $HOME/git/ndb
will be used to create the project.

##Some Akka theory:
Actor: A worker concurrency primitive, which synchronously processes messages. Actors can hold state, which can change.

Message: A piece of data used to communicate with processes (for example, Actors).

Message-passing: A software development paradigm where messages are passed to invoke behavior instead of directly invoking the behavior.

Mailing address: Where messages are sent for an actor to process when the actor is free.

Mailbox: The place messages are stored until an actor is able to process the message. This can be viewed as a queue of messages.

Actor system: A collection of actors, their addresses, mailboxes, and configuration, etc.


##Install project from scratch
```
(go to oracle and download 1.8 jdk java for you maxos)
vim .bash_profile
export JAVA_HOME=$(/usr/libexec/java_home)
(exit)
source .bash_profile
echo $JAVA_HOME
/usr/libexec/java_home -V
(NOT NEEDED < ACTIVATOR DOES IT: install/download scala from scala-lang.org)
brew install typesafe-activator
activator --version
cd git
activator new
(pick scala-minimum)
cd ndb
activator test
(install free version intellij)
```

##To run the test code:
```
cd <project>
activator
clean
test
(The scalatest class , using akka test, will perform test confirming succes when a message could be processed by our Akka actor
```

========= Reactive applications ======================
the four tenets of the Reactive Manifesto (for web applications). These are the qualities that we will strive for in our applications so that they will:
 scale and be resilient to failure. 
We will refer back to these qualities throughout this book. The Reactive Manifesto can be found at http://www.reactivemanifesto.org/.

Responsive

Our applications should respond to requests as fast as possible.

If we have a choice between getting data in a serial manner or in parallel, we should always choose to get the data in parallel in order to get a response back to a user faster. Requests for unrelated data can be made at the same time. When requesting unrelated non-dependent pieces of data, we should evaluate if it's possible to make those requests at the same time.

If there is a potential error situation, we should return a notification of the problem to the user immediately rather than having them wait for the timeout.

Elastic

Our applications should be able to scale under varying workload (especially achieved by adding more computing resources). In order to achieve elasticity, our systems should aim to eliminate bottlenecks.

If we have our in-memory database running on a virtual machine, adding a second machine node could split the queries across the two servers, doubling potential throughput. Adding additional nodes should allow performance to scale in a near-linear manner.

Adding a second in-memory database node could double memory capacity by splitting the data and moving half of it to the new node. Adding nodes should expand capacity in a near-linear manner.

Resilient

Our applications should expect faults to occur and react to them gracefully. If a component of the system fails, it should not cause a lack of availability for requests that do not touch that component. Failure is inevitable—that impact should be isolated to the component that fails. If possible, failure of a component should not cause any impact in behavior by employing replication and redundancy in critical components and data.

Event-driven/message-driven

Using messages instead of method invocation prescribes a way in which we can meet the other three reactive tenets. Message-driven systems move the control over how, when, and where requests are responded, which allows routing and load balancing of the responding component.

An asynchronous message-driven system can more efficiently utilize a system's resources as it only consumes resources like threads when they are actually needed. Messages can be delivered to remote machines as well (location transparency). As messages are queued and delivered outside an actor, it's possible to self-heal a failing system via supervision.
========================================================

Actors are not accessed directly, we get a reference. Actor System manages the abstract layer. By using messages, and not invoking methods pure encapsulation is enforced;
eg: scala> class ScalaPongActor extends Actor { override def receive: Receive = { case "Ping" => sender() ! "Pong" case _ => sender() ! Status.Failure(new Exception("unknown message"))
}

If you want to respond with an response defined by an argument, create a prop factory object, it prevents breaking code if changes are needed:

//Scala
object ScalaPongActor { def props(response: String): Props = { Props(classOf[ScalaPongActor], response) }
}
use: 
//Scala
val actor: ActorRef = actorSystem.actorOf(ScalaPongActor props "PongFoo")

Actorof creates actor and gives actorref}
anothe rway to get reference is using the actor path, a each actor has a path on a local machine, or remote using akka.tcp:some server.
To use it:
ActorSelection selection = system.actorSelection("akka.tcp://actorSystem@host.jason-goodwin.com:5678/user/KeanuReeves");



event driven messages versus blocking io:
Code does not obviously express failure in the response type
Code does not obviously express latency in the response type
Blocking models have throughput limitations due to fixed threadpool sizes
Creating and using many threads has a performance overhead due to context-switching


