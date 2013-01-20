package com

package object scalaequals {
  object Errors {
    val incorrectCanEqualCallSite: String = "Equals.canEqual must be called from within a canEqual method!"
    val incorrectEqualCallSite: String = "Equals.equal must be called from within an equals method!"
    val incorrectHashCallSite: String = "Equals.hash must be called from within a hashCode method!"
    val incorrectHashOrdering: String = "No attachments found on equals. Ensure that the definition of #equals " +
      "comes before #hashCode() and that you are using Equals.equal or Equals.equalAllVals"
    val missingEquals: String =
      "No equals method found! Ensure you are overriding equals and that it has the correct signature."
  }
}
