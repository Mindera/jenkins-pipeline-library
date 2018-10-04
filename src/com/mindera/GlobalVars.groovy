#!/usr/bin/env groovy

package com.mindera

class GlobalVars {
   static String mindera = "www.mindera.com"

   // refer to this in a pipeline using:
   //
   // import com.mindera.GlobalVars
   // println GlobalVars.mindera
}
