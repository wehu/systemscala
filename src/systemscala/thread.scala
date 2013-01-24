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
import scala.util.continuations._

class Thread(var body: ()=>Unit @cps[Unit], n: String = "") {
  Thread.counter += 1
  val name = if (n == "") ("thread " + Thread.counter) else n
  Thread.add(this)
  val parent = if (name == "root") this else Thread.current
  override def toString() = name
  def resume: Unit @cps[Unit] = {
    Thread.current = this
    body()
  }
  def _yield: Unit @cps[Unit] = {
    shift { k: ( Unit => Unit) => {
        body = ()=>{k()}
      }
    }
  }
}

object Thread {
  var counter = 0
  var root : Thread = null
  var current = root
  var queue_r = scala.collection.mutable.HashMap[Thread, Thread]()
  var queue_s = scala.collection.mutable.HashMap[Thread, Thread]()
  def remove(t: Thread){
    queue_r -= t
    queue_s -= t
  }
  def add(t: Thread){
    queue_r += (t -> t)
  }
  def apply(body: => Unit @cps[Unit], name: String = ""): Thread = {
    new Thread(()=>{body}, name)
  }
  def sleep: Unit @cps[Unit] = {
    var c = Thread.current
    queue_r -= c
    queue_s += (c -> c)
    c._yield
  }
  def wake(t: Thread){
    queue_s -= t
    queue_r += (t -> t)
  }
  def run(body: => Unit @cps[Unit]) {
    root = this(body, "root")
    runOne
  }
  def runOne {
    //var rs = queue_r.keys
    while (queue_r.size > 0){
      //for (r <- rs) {
        val (r, _r) = queue_r.head
        Thread.current = root
        remove(r)
        reset {
          r.resume
        }
      //}
      runOne
    }
  }
}
