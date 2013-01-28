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

class Event(n: String = "", tmp: Boolean = false) {
  import scala.util.continuations.cps
  type CB = ()=>Unit
  var cbs = scala.collection.mutable.HashMap[CB, CB]()
  Event.counter += 1
  val name = if (n == "") "tmp$" + Event.counter else n
  if(!tmp)
    Event.add(this)
  override def toString() = "Event " + name
  def subscribe(cb: => Unit) : CB = {
    val _cb = ()=>{cb}
    cbs += (_cb -> _cb)
    _cb
  }
  def _notify {
    cbs foreach ((p)=>{p._1()}) 
  }
  def remove (cb: CB) {
    cbs -= cb
  }
  def tmpEvent: Event = {
    val e = new Event("", tmp)
    var id: CB = null
    id = this.subscribe{
      this.remove(id)
      e._notify
    }
    e
  }
  def _wait(once: Boolean = true) :Unit@cps[Unit]= {
    val c = Thread.current
    var id: CB = null
    id = this.subscribe {
      if(once)
        this.remove(id)
      Thread.wake(c)
    }
    Thread.sleep
  }
  def or(o: Event, tmp: Boolean = true): Event = {
    val e = new Event("", tmp)
    var t: Int = -1
    var id0: CB = null
    var id1: CB = null
    val cb = ()=>{
      if (tmp) {
        this.remove(id0)
        o.remove(id1)
      }
      if (t != SimTime.timeline)
        e._notify
      t = SimTime.timeline
    }
    id0 = this.subscribe {cb()}
    id1 = o.subscribe {cb()}
    e
  }
  def and(o: Event, tmp: Boolean = true): Event = {
    val e = new Event("", tmp)
    var t: Int = -1
    var id0: CB = null
    var id1: CB = null
    val cb = ()=>{
      if (t == SimTime.timeline){
        if (tmp) {
          this.remove(id0)
          o.remove(id1)
        }
        e._notify
      }
      t = SimTime.timeline
    }
    id0 = this.subscribe {cb()}
    id1 = o.subscribe {cb()}
    e
  }
}

object Event {
  var counter = 0
  var insts = scala.collection.mutable.HashMap[String, Event]()
  def apply(name: String = "") : Event = {
    new Event(name)
  }
  def add(e: Event){
    insts.get(e.name) match {
      case None => insts += (e.name -> e)
      case _ => throw new Exception("Attempt to redefine a event " + e.name)
    }
  }
  def event(n: String): Event = {
    insts.get(n) match {
      case None => throw new Exception("Cannot find event " + n)
      case Some(e) => e
    }
  }
  def remove(n: String) {
    insts -= n
  }
  def remove(e: Event) {
    insts -= e.name
  }
}
