import {TestCase} from 'code-altimeter-js'
import '../org/generated/package'
import {Blob, FileReader} from './utils/Blob'
import {FakeHttpRequester} from './utils/FakeHttpRequester'
import {globalFlexioImport} from '@flexio-oss/js-commons-bundle/global-import-registry'

const assert = require('assert')

global.Blob = Blob
global.FileReader = FileReader

class BodiesTest extends TestCase {

  testTypePayload() {
    let requester = new FakeHttpRequester()
    requester.nextBody(new Blob('{"name":"Morillo"}'))

    let client = new globalFlexioImport.org.generated.client.RequestBodiesAPIClient(requester, 'http://gateway')

    let request = new globalFlexioImport.org.generated.api.TypePostRequestBuilder()
    let littleObj = new globalFlexioImport.org.generated.api.types.LittleObjectBuilder()
    littleObj.name('Jungle Patrol')
    request.payload(littleObj.build())

    client.type().typePost(request.build(), (response) => {
      assert.equal(response.status200().payload().name(), 'Morillo')
      assert.equal(requester.lastBody().content(), '{"name":"Jungle Patrol"}')
    })
  }

  testTypeArrayShortPayload() {
    let requester = new FakeHttpRequester()
    requester.nextBody(new Blob('[{"name":"Morillo"}]'))
    let client = new globalFlexioImport.org.generated.client.RequestBodiesAPIClient(requester, 'http://gateway')

    let request = new globalFlexioImport.org.generated.api.TypeArrayShortPostRequestBuilder()
    let littleObj1 = new globalFlexioImport.org.generated.api.types.LittleObjectBuilder()
    littleObj1.name('Morillo')
    let littleObj2 = new globalFlexioImport.org.generated.api.types.LittleObjectBuilder()
    littleObj2.name('Jungle Patrol')

    let list = new globalFlexioImport.org.generated.api.types.LittleObjectList(littleObj1.build(), littleObj2.build())
    request.payload(list)

    client.typeArrayShort().typeArrayShortPost(request.build(), (response) => {
      assert.equal(response.status200().payload()[0].name(), 'Morillo')
      assert.equal(requester.lastBody().content(), '[{"name":"Morillo"},{"name":"Jungle Patrol"}]')
    })
  }

  testTypeArrayPayload() {
    let requester = new FakeHttpRequester()
    requester.nextBody(new Blob('[{"name":"Morillo"}]'))
    let client = new globalFlexioImport.org.generated.client.RequestBodiesAPIClient(requester, 'http://gateway')

    let request = new globalFlexioImport.org.generated.api.TypeArrayPostRequestBuilder()
    let littleObj1 = new globalFlexioImport.org.generated.api.types.LittleObjectBuilder()
    littleObj1.name('Morillo')
    let littleObj2 = new globalFlexioImport.org.generated.api.types.LittleObjectBuilder()
    littleObj2.name('Jungle Patrol')

    let list = new globalFlexioImport.org.generated.api.types.LittleObjectList(littleObj1.build(), littleObj2.build())
    request.payload(list)

    client.typeArray().typeArrayPost(request.build(), (response) => {
      assert.equal(response.status200().payload()[0].name(), 'Morillo')
      assert.equal(requester.lastBody().content(), '[{"name":"Morillo"},{"name":"Jungle Patrol"}]')
    })
  }

  testObjectPayload() {
    let requester = new FakeHttpRequester()
    requester.nextBody(new Blob('{"Romare":"The Blues"}'))
    let client = new globalFlexioImport.org.generated.client.RequestBodiesAPIClient(requester, 'http://gateway')

    let request = new globalFlexioImport.org.generated.api.SingleObjectPostRequestBuilder()
    const obj1 = globalFlexioImport.io.flexio.flex_types.ObjectValue.builder()
      .stringValue('High', 'Klassified')
      .numberValue('1250', 1919)
      .build()
    request.payload(obj1)

    client.singleObject().singleObjectPost(request.build(), (response) => {
      assert.equal(requester.lastBody().content(), '{"1250":1919,"High":"Klassified"}')
      assert.equal(response.status200().payload().stringValue('Romare'), 'The Blues')
    })
  }


  testNullPayload() {
    let requester = new FakeHttpRequester()
    requester.nextBody(new Blob(null))
    let client = new globalFlexioImport.org.generated.client.RequestBodiesAPIClient(requester, 'http://gateway')

    let request = new globalFlexioImport.org.generated.api.SingleObjectPostRequestBuilder()
    const obj1 = globalFlexioImport.io.flexio.flex_types.ObjectValue.builder()
      .stringValue('High', 'Klassified')
      .numberValue('1250', 1919)
      .build()
    request.payload(obj1)

    client.singleObject().singleObjectPost(request.build(), (response) => {
      assert.equal(requester.lastBody().content(), '{"1250":1919,"High":"Klassified"}')
      assert.equal(response.status200().payload(), null)
    })
  }

  testObjectArrayShortPayload() {
    let requester = new FakeHttpRequester()
    requester.nextBody(new Blob('[{"Romare":"The Blues"},{"Eprom":"9 To Ya Dome"}]'))
    let client = new globalFlexioImport.org.generated.client.RequestBodiesAPIClient(requester, 'http://gateway')

    let request = new globalFlexioImport.org.generated.api.ObjectArrayShortPostRequestBuilder()
    const obj1 = globalFlexioImport.io.flexio.flex_types.ObjectValue.builder()
      .stringValue('High', 'Klassified')
      .build()
    const obj2 = globalFlexioImport.io.flexio.flex_types.ObjectValue.builder()
      .numberValue('1250', 1919)
      .build()

    request.payload(new globalFlexioImport.io.flexio.flex_types.arrays.ObjectArray(obj1, obj2))

    client.objectArrayShort().objectArrayShortPost(request.build(), (response) => {
      assert.equal(requester.lastBody().content(), '[{"High":"Klassified"},{"1250":1919}]')
      assert.equal(response.status200().payload()[0].stringValue('Romare'), 'The Blues')
      assert.equal(response.status200().payload()[1].stringValue('Eprom'), '9 To Ya Dome')
    })
  }

  testObjectArrayPayload() {
    let requester = new FakeHttpRequester()
    requester.nextBody(new Blob('[{"Romare":"The Blues"},{"Eprom":"9 To Ya Dome"}]'))
    let client = new globalFlexioImport.org.generated.client.RequestBodiesAPIClient(requester, 'http://gateway')

    let request = new globalFlexioImport.org.generated.api.ObjectArrayPostRequestBuilder()
    const obj1 = globalFlexioImport.io.flexio.flex_types.ObjectValue.builder()
      .stringValue('High', 'Klassified')
      .build()
    const obj2 = globalFlexioImport.io.flexio.flex_types.ObjectValue.builder()
      .numberValue('1250', 1919)
      .build()


    request.payload(new globalFlexioImport.io.flexio.flex_types.arrays.ObjectArray(obj1, obj2))

    client.objectArray().objectArrayPost(request.build(), (response) => {
      assert.equal(requester.lastBody().content(), '[{"High":"Klassified"},{"1250":1919}]')
      assert.equal(response.status200().payload()[0].stringValue('Romare'), 'The Blues')
      assert.equal(response.status200().payload()[1].stringValue('Eprom'), '9 To Ya Dome')
    })
  }


  testFilePayload() {
    let requester = new FakeHttpRequester()
    requester.nextBody(new Blob('hello'))
    let client = new globalFlexioImport.org.generated.client.RequestBodiesAPIClient(requester, 'http://gateway')

    let request = new globalFlexioImport.org.generated.api.FilePostRequestBuilder()

    request.payload(new Blob('this is binary data'))
    request.contentType('Toto')

    client.file().filePost(request.build(), (response) => {
      assert.equal(response.status200().payload().content(), 'hello')
      assert.equal(requester._lastContentType, 'Toto')
      assert.equal(response.status200().contentType(), 'Shit')
      assert.equal(requester.lastBody().content(), 'this is binary data')
    })
  }

}

runTest(BodiesTest)
