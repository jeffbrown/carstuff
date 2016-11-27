package demo

import grails.plugins.rest.client.RestBuilder
import grails.test.mixin.integration.Integration
import org.springframework.http.HttpStatus
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Integration
@Stepwise
class CarFunctionalSpec extends Specification {

    @Shared
    def rest = new RestBuilder()

    void "test that no cars exist"() {
        when:
        def resp = rest.get("http://localhost:${serverPort}/automobiles")
        def contentType = resp.headers.getContentType()


        then:
        resp.status == HttpStatus.OK.value()
        contentType.subtype == 'json'
        contentType.type == 'application'
        resp.json.size() == 0
    }

    void "test creating a car"() {
        when:
        def resp = rest.post("http://localhost:${serverPort}/automobiles") {
            json {
                make = 'Chevy'
                model = 'Equinox'
            }
        }
        def contentType = resp.headers.getContentType()

        then:
        resp.status == HttpStatus.CREATED.value()
        contentType.subtype == 'json'
        contentType.type == 'application'
        resp.json.make == 'Chevy'
        resp.json.model == 'Equinox'

        when:
        resp = rest.post("http://localhost:${serverPort}/automobiles") {
            json {
                make = 'Ford'
                model = 'Fusion'
            }
        }
        contentType = resp.headers.getContentType()

        then:
        resp.status == HttpStatus.CREATED.value()
        contentType.subtype == 'json'
        contentType.type == 'application'

        and:
        resp.json.make == 'Ford'
        resp.json.model == 'Fusion'
    }

    void 'test retrieving list of cars defaults to JSON'() {
        when:
        def resp = rest.get("http://localhost:${serverPort}/automobiles")
        def contentType = resp.headers.getContentType()

        then:
        resp.status == HttpStatus.OK.value()
        contentType.subtype == 'json'
        contentType.type == 'application'
        resp.json.size() == 2

        and:
        resp.json[0].make == 'Chevy'
        resp.json[0].model == 'Equinox'

        and:
        resp.json[1].make == 'Ford'
        resp.json[1].model == 'Fusion'

    }

    void 'test retrieving list of cars as JSON'() {
        when:
        def resp = rest.get("http://localhost:${serverPort}/automobiles.json")
        def contentType = resp.headers.getContentType()

        then:
        resp.status == HttpStatus.OK.value()
        contentType.subtype == 'json'
        contentType.type == 'application'
        resp.json.size() == 2

        and:
        resp.json[0].make == 'Chevy'
        resp.json[0].model == 'Equinox'

        and:
        resp.json[1].make == 'Ford'
        resp.json[1].model == 'Fusion'
    }

    void 'test retrieving list of cars as XML'() {
        when:
        def resp = rest.get("http://localhost:${serverPort}/automobiles.xml")
        def contentType = resp.headers.getContentType()

        then:
        resp.status == HttpStatus.OK.value()
        contentType.subtype == 'xml'
        contentType.type == 'text'
        resp.json.car.size() == 2

        and:
        resp.json.car[0].make.text() == 'Chevy'
        resp.json.car[0].model.text() == 'Equinox'

        and:
        resp.json.car[1].make.text() == 'Ford'
        resp.json.car[1].model.text() == 'Fusion'
    }
}
