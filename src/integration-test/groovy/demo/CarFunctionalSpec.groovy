package demo

import static grails.http.HttpHeader.*
import static grails.http.HttpStatus.*
import static grails.http.MediaType.*
import grails.http.MediaType
import grails.http.client.RxHttpClientBuilder
import grails.test.mixin.integration.Integration
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Integration
@Stepwise
class CarFunctionalSpec extends Specification {

    @Shared RxHttpClientBuilder rest = new RxHttpClientBuilder()

    void "test that no cars exist"() {
        when:
        def resp = rest.get("http://localhost:${serverPort}/automobiles").toBlocking().first()

        then:
        resp.status == OK
        resp.header(CONTENT_TYPE.toString()).contains JSON.toString()
        resp.toJson().toBlocking().first().size() == 0
    }

    void "test creating a car"() {
        when:
        def resp = rest.post("http://localhost:${serverPort}/automobiles") {
            json {
                make 'Chevy'
                model 'Equinox'
            }
        }.toBlocking().first()

        def jsonResult = resp.toJson().toBlocking().first()

        then:
        resp.status == CREATED
        resp.header(CONTENT_TYPE.toString()).contains JSON.toString()
        jsonResult.make == 'Chevy'
        jsonResult.model == 'Equinox'

        when:
        resp = rest.post("http://localhost:${serverPort}/automobiles") {
            json {
                make 'Ford'
                model 'Fusion'
            }
        }.toBlocking().first()

        jsonResult = resp.toJson().toBlocking().first()

        then:
        resp.status == CREATED
        resp.header(CONTENT_TYPE.toString()).contains JSON.toString()

        and:
        jsonResult.make == 'Ford'
        jsonResult.model == 'Fusion'
    }

    void 'test retrieving list of cars defaults to JSON'() {
        when:
        def resp = rest.get("http://localhost:${serverPort}/automobiles").toBlocking().first()
        def contentType = resp.getHeader(CONTENT_TYPE.toString())
        def jsonResult = resp.toJson().toBlocking().first()
        then:
        resp.status == OK
        contentType.contains JSON.toString()
        jsonResult.size() == 2

        and:
        jsonResult[0].make == 'Chevy'
        jsonResult[0].model == 'Equinox'

        and:
        jsonResult[1].make == 'Ford'
        jsonResult[1].model == 'Fusion'

    }

    void 'test retrieving list of cars as JSON'() {
        when:
        def resp = rest.get("http://localhost:${serverPort}/automobiles.json").toBlocking().first()
        def contentType = resp.getHeader(CONTENT_TYPE.toString())
        def jsonResult = resp.toJson().toBlocking().first()

        then:
        resp.status == OK
        contentType.contains JSON.toString()
        jsonResult.size() == 2

        and:
        jsonResult[0].make == 'Chevy'
        jsonResult[0].model == 'Equinox'

        and:
        jsonResult[1].make == 'Ford'
        jsonResult[1].model == 'Fusion'
    }

    void 'test retrieving list of cars as XML'() {
        when:
        def resp = rest.get("http://localhost:${serverPort}/automobiles.xml").toBlocking().first()
        def contentType = resp.getHeader(CONTENT_TYPE.toString())
        def xmlResult = resp.toXml().toBlocking().first()


        then:
        resp.status == OK
        contentType.contains TEXT_XML.toString()
        xmlResult.car.size() == 2

        and:
        xmlResult.car[0].make.text() == 'Chevy'
        xmlResult.car[0].model.text() == 'Equinox'

        and:
        xmlResult.car[1].make.text() == 'Ford'
        xmlResult.car[1].model.text() == 'Fusion'
    }
}
