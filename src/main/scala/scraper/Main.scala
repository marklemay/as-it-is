package scraper

import org.apache.http.client.fluent.Request
import org.apache.http.client.utils.URLEncodedUtils
import jpathwrapper.Jpath
import scala.io.Source
import scala.reflect.io.Path
import scala.reflect.io.File
import java.net.URLEncoder
import org.apache.commons.lang3.StringUtils.substringAfterLast

object Main {

  def jp[T](json: String, jp: String): List[T] = {

    import scala.collection.JavaConversions._

    return Jpath.jp[java.util.List[T]](json, jp).toList;
  }

   import Config._;
    

  
  def getBuilds(json:String):Set[BuildSystem] ={
    var buildSystems = Set[BuildSystem]();
    
              //"path": "pom.xml",
          //"type": "file", //TODO: confirm that each of these is a file

    
          val allpaths = jp[java.lang.String](json, "$.tree[*].path");

          
          val allnames =allpaths.map{s=>(substringAfterLast("/"+s,"/"),s)}.toSet

    allnames.filter { p => p._1 == "pom.xml" }.foreach { p =>
      buildSystems = buildSystems ++ Set(maven)
      println(p._2)
    }

    allnames.filter { p => p._1 == "build.gradle" }.foreach { p =>
      buildSystems = buildSystems ++ Set(gradle)
      println(p._2)
    }

    allnames.filter { p => p._1 == "pom.xml" }.foreach { p =>
      buildSystems = buildSystems ++ Set(maven)
      println(p._2)
    }

    allnames.filter { p => p._1 == "build.xml" }.foreach { p =>
      buildSystems = buildSystems ++ Set(ant)
      println(p._2)
    }

    allnames.filter { p => p._1 == ".project" }.foreach { p =>
      buildSystems = buildSystems ++ Set(ant)
      println(p._2)
    }
          
//          if (allnames.filter((n,p)=>).contains("pom.xml")) {
//            buildSystems = buildSystems ++ Set(maven)
//          }
//
//          if (allnames.contains("build.gradle")) {
//            buildSystems = buildSystems ++ Set(gradle)
//          }
//
//          if (allnames.contains("build.xml")) {
//            buildSystems = buildSystems ++ Set(ant)
//          }
//          
//          if (allnames.contains(".project")) {
//            buildSystems = buildSystems ++ Set(eclipse)
//          }
    
    return buildSystems
  }
    
  
  def main(args: Array[String]) {
    println("hi?????")

    var map  = Map[Set[BuildSystem],Int]().withDefault(x=>0)
    
    Source.fromFile(raw"C:\Users\christ\git\as-it-is\src\test\resources\java").getLines().foreach {
      ln =>
        {

          val part = ln.replaceFirst("https://github.com/", "")
          print(part)
          val url = "https://api.github.com/repos/" + part + "/git/trees/master?recursive=1&"+client //FIXME: use defualt branch, might not be master

          try{//FIXME: remove this block
          val response = Request.Get(url + "?"+client)
            .execute().returnContent();

          val buildSystems = getBuilds( response.asString())

print(buildSystems.mkString(" [","] [", "]"))
          
          map += buildSystems -> (map(buildSystems)+1)
println
val total:Float = map.map{case (s,n) =>n}.fold(0)(_+_)

          println(map.map{case (s,n) => s.mkString(" [","] [", "]")+":\t"+n+"\t("+ (n.asInstanceOf[Float]/total)+")"}.mkString("\n"))
          println

          //Thread.sleep(2000L) // rate limited
          }catch {
  case t => t.printStackTrace()// todo: handle error
}
          /*
          noticed
          

          Rakefile:
           - sparklemotion\nokogiri
          Makefile
           - jeresig\processing-js
          .project
           - cyrilmottier\GreenDroid
           - /OpenRefine/OpenRefine
           - koush\AndroidAsync
           - jgilfelt/android-viewbadger
           thest1/LazyList
          project.clj
           - videlalvaro\gifsockets
          build.xml (ant)
           - nicolasgramlich\AndEngine
           - /OpenRefine/OpenRefine
           - apache/cassandra
           - yui\yuicompressor
           - voldemort/voldemort
           - ...
          projects within diretories:
           - excilys\androidannotations
           - aporter\coursera-android
           - koush\ion
           - pakerfeldt\android-viewflow
           - johannilsson\android-actionbar
           - commonsguy\cw-omnibus (2 levels)
           - openaphid\android-flip
           - bigbluebutton\bigbluebutton
           - commonsguy\cw-advandroid (2 deep)
           ....
          none?
           - johannilsson/android-pulltorefresh
           - android\platform_frameworks_base
           - xamarin\XobotOS
           - phonegap\phonegap-facebook-plugin
           - processing\processing
           - douglascrockford\JSON-java
           - ether/pad
           - BonzaiThePenguin/WikiSort
           commonsguy/cw-android
           novoda/android
          not a proj:
           - purplecabbage\phonegap-plugins
           
           check next: novoda/android
          
          * 
          * 
          * 
          * 
          * 
          * latest
peter-lawrey/Java-Chronicle [maven]
 []:	12	(0.08108108)
 [maven] [gradle] [eclipse]:	4	(0.027027028)
 [maven] [gradle] [ant]:	2	(0.013513514)
 [gradle] [ant]:	2	(0.013513514)
 [maven] [ant]:	6	(0.04054054)
 [maven]:	45	(0.30405405)
 [ant] [eclipse]:	9	(0.06081081)
 [maven] [eclipse]:	6	(0.04054054)
 [maven] [gradle] [ant] [eclipse]:	4	(0.027027028)
 [ant]:	4	(0.027027028)
 [gradle] [ant] [eclipse]:	5	(0.033783782)
 [gradle]:	26	(0.17567568)
 [eclipse]:	7	(0.0472973)
 [ant] [maven] [eclipse]:	6	(0.04054054)
 [maven] [gradle]:	7	(0.0472973)
 [gradle] [eclipse]:	3	(0.02027027)

TooTallNate/Java-WebSocketException in thread "main" org.apache.http.client.HttpResponseException: Not Found
	at org.apache.http.client.fluent.ContentResponseHandler.handleResponse(ContentResponseHandler.java:47)
	at org.apache.http.client.fluent.ContentResponseHandler.handleResponse(ContentResponseHandler.java:40)
	at org.apache.http.client.fluent.Response.handleResponse(Response.java:90)
	at org.apache.http.client.fluent.Response.returnContent(Response.java:97)
	at scraper.Main$$anonfun$getBuilds$1.apply(Main.scala:36)
	at scraper.Main$$anonfun$getBuilds$1.apply(Main.scala:29)
	at scala.collection.TraversableLike$$anonfun$map$1.apply(TraversableLike.scala:245)
	at scala.collection.TraversableLike$$anonfun$map$1.apply(TraversableLike.scala:245)
	at scala.collection.immutable.List.foreach(List.scala:302)
	at scala.collection.TraversableLike$class.map(TraversableLike.scala:245)
	at scala.collection.AbstractTraversable.map(Traversable.scala:104)
	at scraper.Main$.getBuilds(Main.scala:29)
	at scraper.Main$$anonfun$main$1.apply(Main.scala:108)
	at scraper.Main$$anonfun$main$1.apply(Main.scala:97)
	at scala.collection.Iterator$class.foreach(Iterator.scala:743)
	at scala.collection.AbstractIterator.foreach(Iterator.scala:1174)
	at scraper.Main$.main(Main.scala:96)
	at scraper.Main.main(Main.scala)
          * 
          * 
          * 
          * TODO: URL encode
          */
          //also check:
          //         X build.gradle
          //          gradle.properties
          //          settings.gradle
          //          maven_push.gradle
          //          *.bat
          //    https://github.com/sparklemotion/nokogiri whatever that is      

          //TODO: compare project to SourceForge, google code, Gitorious?, ...

          
          
          
          
        }

    }

  }

  //FIXME: there is definitely a claner scala way to do this
//  TODO: need to get "master_branch" too
  def getall(lang: String, Stars: Int): List[String] = {
    var all_urls = List[String]();

    var again: Boolean = true;
    var i = 1;

    do {
      val response = Request.Get("https://api.github.com/search/repositories?"
        + "q=language:"
        //        + "scala"
        + "java"
        + "+stars:"
        + "%3E1"
        // >1
        + "&sort=stars&order=desc"
        + "&page=" + i
        //pagination starts at 1
        + "&per_page=100" //NOTE: they seem to cap us at 100
        )
        .execute().returnContent();

      //TODO: save this for record

      //println(response)

      val json = response.asString()

      import scala.collection.JavaConversions._

      val urls = Jpath.jp[java.util.List[String]](json, "$.items[*].html_url").toList;
      println(urls.mkString("\n"))
      all_urls ++= urls;

      again &&= (urls.size > 0);

      Thread.sleep(60000L / 5l) // rate limited
      //TODO: if we auth we can pump this up
      i += 1;
    } while (again)
    //TODO: how often do people actually use do...whiles?
    //did projects start with maven?

    return all_urls;
  }
}