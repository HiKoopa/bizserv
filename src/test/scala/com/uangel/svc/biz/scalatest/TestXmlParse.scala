package com.uangel.svc.biz.scalatest

import com.uangel.svc.biz.cti.LoginResp
import com.uangel.svc.biz.impl.ctinetty.{CtiMessageParser, CtiXmlHandler}
import org.scalatest.funsuite.AnyFunSuite
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

import java.io.ByteArrayInputStream
import javax.xml.parsers.SAXParserFactory
import scala.util.Success

class Handler extends  DefaultHandler {
  override def startElement(uri: String, localName: String, qName: String, attributes: Attributes): Unit = {
    println(s"start qname = $qName")
    if (qName == "LoginResp") {
      val v = attributes.getValue("Result")
      println(s"Result = $v")
    }
  }


  override def endElement(uri: String, localName: String, qName: String): Unit = {
    println(s"endElement $qName")
  }

  override def characters(ch: Array[Char], start: Int, length: Int): Unit = {
    val str = new String(ch, start, length)
    println(s"text = $str")
  }
}
class TestXmlParse extends AnyFunSuite {
  test("xml parsing success") {
    val xml =
      """<?xml version='1.0' encoding='ISO-8859-1'?>
        |<!DOCTYPE GctiMsg SYSTEM 'IServer.dtd'>
        |   <GctiMsg>
        |     <CallId>DEPVARS</CallId>
        |     <LoginResp IServerVer='IVR Server:8.1.001.02' Result='Success' Status='OK'/>
        |   </GctiMsg>""".stripMargin


    var parser = new CtiMessageParser()

    var parsed = parser.parse( xml.getBytes)

    assert(parsed.isSuccess)
    assert(parsed.get().messageType() == "LoginResp")

    var loginResp = parsed.get().asInstanceOf[LoginResp]
    assert(loginResp.getCallID == "DEPVARS" )
    assert(loginResp.getStatus == "OK")

  }

  test("xml parsing mandatory parameter missing") {
    var xml =
      """<?xml version='1.0' encoding='ISO-8859-1'?>
        |<!DOCTYPE GctiMsg SYSTEM 'IServer.dtd'>
        |   <GctiMsg>
        |     <CallId>DEPVARS</CallId>
        |     <LoginResp IServerVer='IVR Server:8.1.001.02' Status='OK'/>
        |   </GctiMsg>""".stripMargin


    var parser = new CtiMessageParser()

    var parsed = parser.parse( xml.getBytes)

    assert(!parsed.isSuccess)

    parsed.failed().get().printStackTrace()

  }

  test("xml parsing no message type") {
    var xml =
      """<?xml version='1.0' encoding='ISO-8859-1'?>
        |<!DOCTYPE GctiMsg SYSTEM 'IServer.dtd'>
        |   <GctiMsg>
        |     <CallId>DEPVARS</CallId>
        |   </GctiMsg>""".stripMargin


    var parser = new CtiMessageParser()

    var parsed = parser.parse( xml.getBytes)

    assert(!parsed.isSuccess)

    parsed.failed().get().printStackTrace()

  }

  test("xml parsing no gcti element") {
    var xml =
      """<?xml version='1.0' encoding='ISO-8859-1'?>
        |<!DOCTYPE GctiMsg SYSTEM 'IServer.dtd'>
        |     <CallId>DEPVARS</CallId>
        |     <LoginResp IServerVer='IVR Server:8.1.001.02' Status='OK'/>
        |   """.stripMargin


    var parser = new CtiMessageParser()

    var parsed = parser.parse( xml.getBytes)

    assert(!parsed.isSuccess)

    parsed.failed().get().printStackTrace()

  }
}