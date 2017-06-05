package com.nossin.ndb.messages

/**
  * Created by michelnossin on 05-06-17.
  */
object Calculator {
  case class Add(a: Int, b: Int)
  case class Sub(a: Int, b: Int)
  case class Div(a: Int, b: Int)
}
