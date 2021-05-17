package com.uangel.svc.biz.scalatest

import com.uangel.svc.biz.impl.ctinetty.{LoginResp, CtiXmlHandler}
import org.scalatest.funsuite.AnyFunSuite
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

import java.io.ByteArrayInputStream
import javax.xml.parsers.SAXParserFactory

class Handler extends  DefaultHandler {
  override def startElement(uri: String, localName: String, qName: String, attributes: Attributes): Unit = {
    println(s"start qname = ${qName}")
    if (qName == "LoginResp") {
      val v = attributes.getValue("Result")
      println(s"Result = ${v}")
    }
  }


  override def endElement(uri: String, localName: String, qName: String): Unit = {
    println(s"endElement ${qName}")
  }

  override def characters(ch: Array[Char], start: Int, length: Int): Unit = {
    val str = new String(ch, start, length)
    println(s"text = ${str}")
  }
}
class TestXmlParse extends AnyFunSuite {
  test("xml parsing") {
    val factory = SAXParserFactory.newInstance()
    //factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)

    factory.setFeature("http://xml.org/sax/features/external-general-entities", false)
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    factory.setXIncludeAware(false)

    val parser = factory.newSAXParser()

    val xml =
      """<?xml version='1.0' encoding='ISO-8859-1'?>
        |<!DOCTYPE GctiMsg SYSTEM 'IServer.dtd'>
        |   <GctiMsg>
        |     <CallId>DEPVARS</CallId>
        |     <LoginResp IServerVer='IVR Server:8.1.001.02' Result='Success' Status='OK'/>
        |   </GctiMsg>""".stripMargin


    val bis = new ByteArrayInputStream(xml.getBytes)
    val handler = new CtiXmlHandler()
    parser.parse( bis , handler)

    assert(handler.getParsed.isPresent)
    assert(handler.getParsed.get().messageType() == "LoginResp")

    val loginResp = handler.getParsed.get().asInstanceOf[LoginResp]
    assert(loginResp.getCallID == "DEPVARS" )
    assert(loginResp.getStatus.isPresent)
    assert(loginResp.getStatus.get() == "OK")

  }
}