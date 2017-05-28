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
