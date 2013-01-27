package org.scalaequals.test

import org.scalaequals.ScalaEquals.{Equal, GenString}

class ParameterizedClass[A, B](val x: A, val y: B) extends Equal with GenString