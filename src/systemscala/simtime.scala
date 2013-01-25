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

class SimTime(val delay: Int = 0) extends Event {
  val simtime = SimTime.timeline + delay * SimTime.timescale
  SimTime.add(this)
  override def toString() = "Simtime " + simtime
  override def _notify {
    super._notify
    Event.remove(this)
  }
}

object SimTime {
  type Queue = scala.collection.mutable.Queue[SimTime]
  var timeline = 0
  var timescale = 1
  var sts = scala.collection.mutable.HashMap[Int, Queue]()

  def apply(delay: Int = 0) : SimTime = {
    new SimTime(delay)
  }
  def add(s: SimTime) {
    sts.get(s.simtime) match {
      case None => var q = new Queue
        q += s
        sts += (s.simtime -> q) 
      case Some(q) => q += s
    }
  }
  def delta(d: Int) = SimTime(d)
  def getRecents: Option[Queue] = {
    if (sts.size > 0) {
      val t = sts.keys.min
      val Some(ss) = sts.get(t)
      sts -= t
      timeline = t
      Some(ss)
    } else {
      None
    }
  }
}

