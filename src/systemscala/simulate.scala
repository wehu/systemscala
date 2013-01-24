/*
* Copyright 2013 The SystemScala Authors. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package systemscala

abstract class Simulate extends App {
  def run
  Simulate.run {run}
}

object Simulate {
  class UserStopException(msg: String) extends Exception(msg)
  def stop {
    throw new UserStopException("Simulation finished by user")
  }
  def run(body: => Unit){
    println("###########################")
    println("## SystemScala Simulator ##")
    println("##  Copyright@wehu 2013  ##")
    println("###########################")
    Logger.info("Simulation started")
    try {
      Thread.run{
        var sl:()=>Unit = null
        sl = ()=>{
          Thread.runOne
          Signal.sync
          SimTime.getRecents match {
            case Some(ss) =>
              ss foreach (_._notify)
              Thread{sl()}
            case None => null
          }
        } : Unit
        body
        Component.run
        sl()
      }
    } catch {
      case e: UserStopException => Logger.info(e.getMessage())
      case e => throw e
    }
  }
}