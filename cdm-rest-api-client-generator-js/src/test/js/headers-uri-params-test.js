import {TestCase} from '@flexio-oss/code-altimeter-js'
import '../org/generated/package'
import {FakeHttpRequester} from './utils/FakeHttpRequester'
import {globalFlexioImport} from '@flexio-oss/js-commons-bundle/global-import-registry'
import {FlexDate, FlexDateTime, FlexTime} from '@flexio-oss/js-commons-bundle/flex-types'
import {Blob, FileReader} from './utils/Blob'

const assert = require('assert')

global.Blob = Blob
global.FileReader = FileReader

class PayloadAndParameterTest extends TestCase {

  testObjectPayload() {
    let requester = new FakeHttpRequester()
    requester._responseHeaders['string-param'] = 'resp-stringParam'
    requester._responseHeaders['stringArrayParam'] = ['v1', 'v2']
    requester._responseHeaders['intParam'] = '7'
    requester._responseHeaders['intArrayParam'] = ['7', '8']
    requester._responseHeaders['floatParam'] = '5.4'
    requester._responseHeaders['floatArrayParam'] = ['5.4', '5.6']
    requester._responseHeaders['dateParam'] = '1993-10-17'
    requester._responseHeaders['dateArrayParam'] = ['1993-10-17', '1992-10-17']
    requester._responseHeaders['datetimeParam'] = '1992-10-17T18:42:07'
    requester._responseHeaders['datetimeArrayParam'] = ['1992-10-17T18:42:07', '1991-10-24T18:42:07']
    requester._responseHeaders['timeParam'] = '17:14:12'
    requester._responseHeaders['timeArrayParam'] = ['17:14:12', '02:14:12']
    requester._responseHeaders['boolParam'] = 'true'
    requester._responseHeaders['boolArrayParam'] = ['true']

    let client = new globalFlexioImport.org.generated.client.SimpleResourcesAPIClient(requester, 'http://gateway')
    let request = new globalFlexioImport.org.generated.api.HeaderParamsGetRequestBuilder()

    request.uriParams('myUriParam')
//    TODO encode uri param
//    request.uriParams('é/à')

    request.stringParam('myStringParam')
    request.stringArrayParam(
      new globalFlexioImport.io.flexio.flex_types.arrays.StringArray('tata', 'yoyo')
    )
    request.intParam(7)
    request.intArrayParam(
      new globalFlexioImport.io.flexio.flex_types.arrays.LongArray(1, 2)
    )
    request.floatParam(7.1)
    request.floatArrayParam(
      new globalFlexioImport.io.flexio.flex_types.arrays.FloatArray(1.3, 22.2)
    )
    request.dateParam(new FlexDate('1992-10-17'))
    request.dateArrayParam(
      new globalFlexioImport.io.flexio.flex_types.arrays.DateArray(new FlexDate('1992-10-02'), new FlexDate('1992-10-17'))
    )
    request.datetimeParam(new FlexDateTime('1992-10-17T14:12:07'))
    request.datetimeArrayParam(
      new globalFlexioImport.io.flexio.flex_types.arrays.DateTimeArray(new FlexDateTime('1992-10-17T14:12:07'), new FlexDateTime('1992-10-02T13:00:00'))
    )
    request.timeParam(new FlexTime('14:12:07'))
    request.timeArrayParam(
      new globalFlexioImport.io.flexio.flex_types.arrays.TimeArray(new FlexTime('14:12:07'), new FlexTime('13:00:00'))
    )
    request.boolParam(true)
    request.boolArrayParam(
      new globalFlexioImport.io.flexio.flex_types.arrays.BooleanArray(true, true, false)
    )

    client.headerParams().headerParamsGet(request.build(), (response) => {
      assert.deepStrictEqual(response.status200().stringParam(), 'resp-stringParam')
      assert.deepStrictEqual(response.status200().stringArrayParam()[0], 'v1')
      assert.deepStrictEqual(response.status200().stringArrayParam()[1], 'v2')
      assert.deepStrictEqual(response.status200().intParam(), 7)
      assert.deepStrictEqual(response.status200().intArrayParam()[0], 7)
      assert.deepStrictEqual(response.status200().intArrayParam()[1], 8)
      assert.deepStrictEqual(response.status200().floatParam(), 5.4)
      assert.deepStrictEqual(response.status200().floatArrayParam()[0], 5.4)
      assert.deepStrictEqual(response.status200().floatArrayParam()[1], 5.6)
      assert.deepStrictEqual(response.status200().dateParam().toJSON(), '1993-10-17')
      assert.deepStrictEqual(response.status200().dateArrayParam()[0].toJSON(), '1993-10-17')
      assert.deepStrictEqual(response.status200().dateArrayParam()[1].toJSON(), '1992-10-17')
      assert.deepStrictEqual(response.status200().datetimeParam().toJSON(), '1992-10-17T18:42:07')
      assert.deepStrictEqual(response.status200().datetimeArrayParam()[0].toJSON(), '1992-10-17T18:42:07')
      assert.deepStrictEqual(response.status200().datetimeArrayParam()[1].toJSON(), '1991-10-24T18:42:07')
      assert.deepStrictEqual(response.status200().timeParam().toJSON(), '17:14:12')
      assert.deepStrictEqual(response.status200().timeArrayParam()[0].toJSON(), '17:14:12')
      assert.deepStrictEqual(response.status200().timeArrayParam()[1].toJSON(), '02:14:12')
      assert.deepStrictEqual(response.status200().boolParam(), true)
      assert.deepStrictEqual(response.status200().boolArrayParam()[0], true)

      assert.deepStrictEqual(requester._headers['string-param'], 'myStringParam')
      assert.deepStrictEqual(requester._headers['stringArrayParam'][0], 'tata')
      assert.deepStrictEqual(requester._headers['stringArrayParam'][1], 'yoyo')
      assert.deepStrictEqual(requester._headers['intParam'], '7')
      assert.deepStrictEqual(requester._headers['intArrayParam'][0], '1')
      assert.deepStrictEqual(requester._headers['intArrayParam'][1], '2')
      assert.deepStrictEqual(requester._headers['floatParam'], '7.1')
      assert.deepStrictEqual(requester._headers['floatArrayParam'][0], '1.3')
      assert.deepStrictEqual(requester._headers['floatArrayParam'][1], '22.2')
      assert.deepStrictEqual(requester._headers['dateParam'], '1992-10-17')
      assert.deepStrictEqual(requester._headers['dateArrayParam'][0], '1992-10-02')
      assert.deepStrictEqual(requester._headers['dateArrayParam'][1], '1992-10-17')
      assert.deepStrictEqual(requester._headers['datetimeParam'], '1992-10-17T14:12:07')
      assert.deepStrictEqual(requester._headers['datetimeArrayParam'][0], '1992-10-17T14:12:07')
      assert.deepStrictEqual(requester._headers['datetimeArrayParam'][1], '1992-10-02T13:00:00')
      assert.deepStrictEqual(requester._headers['timeParam'], '14:12:07')
      assert.deepStrictEqual(requester._headers['timeArrayParam'][0], '14:12:07')
      assert.deepStrictEqual(requester._headers['timeArrayParam'][1], '13:00:00')
      assert.deepStrictEqual(requester._headers['boolParam'], 'true')
      assert.deepStrictEqual(requester._headers['boolArrayParam'][0], 'true')
      assert.deepStrictEqual(requester._headers['boolArrayParam'][1], 'true')
      assert.deepStrictEqual(requester._headers['boolArrayParam'][2], 'false')

      assert.deepStrictEqual(requester._path, 'http://gateway/header-params/myUriParam')
//    TODO encode uri param
//      assert.deepStrictEqual(requester._path, 'http://gateway/header-params/%C3%A9%2F%C3%A0')
    })
  }

  testDoubleUriParam() {
    let requester = new FakeHttpRequester()
    let client = new globalFlexioImport.org.generated.client.SimpleResourcesAPIClient(requester, 'http://gateway')
    let request = new globalFlexioImport.org.generated.api.ParamsArrayGetRequestBuilder()
    let uriParams = new globalFlexioImport.io.flexio.flex_types.arrays.StringArray('p1', 'p2')
    request.uriParams(uriParams)

    request.stringParam('myStringParam')
    request.stringArrayParam(
      new globalFlexioImport.io.flexio.flex_types.arrays.StringArray('tata', 'yoyo')
    )
    request.intParam(7)
    request.intArrayParam(
      new globalFlexioImport.io.flexio.flex_types.arrays.LongArray(1, 2)
    )
    request.floatParam(7.1)
    request.floatArrayParam(
      new globalFlexioImport.io.flexio.flex_types.arrays.FloatArray(1.3, 22.2)
    )
    request.dateParam(new FlexDate('1992-10-17'))
    request.dateArrayParam(
      new globalFlexioImport.io.flexio.flex_types.arrays.DateArray(new FlexDate('1992-10-02'), new FlexDate('1992-10-17'))
    )
    request.datetimeParam(new FlexDateTime('1992-10-17T14:12:07'))
    request.datetimeArrayParam(
      new globalFlexioImport.io.flexio.flex_types.arrays.DateTimeArray(new FlexDateTime('1992-10-17T14:12:07'), new FlexDateTime('1992-10-02T13:00:00'))
    )
    request.timeParam(new FlexTime('14:12:07'))
    request.timeArrayParam(
      new globalFlexioImport.io.flexio.flex_types.arrays.TimeArray(new FlexTime('14:12:07'), new FlexTime('13:00:00'))
    )
    request.boolParam(true)
    request.boolArrayParam(
      new globalFlexioImport.io.flexio.flex_types.arrays.BooleanArray(true, true, false)
    )

    client.headerParams().paramsArray().paramsArrayGet(request.build(), (response) => {
      assert.deepStrictEqual(requester._path, 'http://gateway/header-params/p1/p2')
      assert.deepStrictEqual(requester._parameters['string-param'], 'myStringParam')
      assert.deepStrictEqual(requester._parameters['stringArrayParam'][0], 'tata')
      assert.deepStrictEqual(requester._parameters['stringArrayParam'][1], 'yoyo')
      assert.deepStrictEqual(requester._parameters['intParam'], '7')
      assert.deepStrictEqual(requester._parameters['intArrayParam'][0], '1')
      assert.deepStrictEqual(requester._parameters['intArrayParam'][1], '2')
      assert.deepStrictEqual(requester._parameters['floatParam'], '7.1')
      assert.deepStrictEqual(requester._parameters['floatArrayParam'][0], '1.3')
      assert.deepStrictEqual(requester._parameters['floatArrayParam'][1], '22.2')
      assert.deepStrictEqual(requester._parameters['dateParam'], '1992-10-17')
      assert.deepStrictEqual(requester._parameters['dateArrayParam'][0], '1992-10-02')
      assert.deepStrictEqual(requester._parameters['dateArrayParam'][1], '1992-10-17')
      assert.deepStrictEqual(requester._parameters['datetimeParam'], '1992-10-17T14:12:07')
      assert.deepStrictEqual(requester._parameters['datetimeArrayParam'][0], '1992-10-17T14:12:07')
      assert.deepStrictEqual(requester._parameters['datetimeArrayParam'][1], '1992-10-02T13:00:00')
      assert.deepStrictEqual(requester._parameters['timeParam'], '14:12:07')
      assert.deepStrictEqual(requester._parameters['timeArrayParam'][0], '14:12:07')
      assert.deepStrictEqual(requester._parameters['timeArrayParam'][1], '13:00:00')
      assert.deepStrictEqual(requester._parameters['boolParam'], 'true')
      assert.deepStrictEqual(requester._parameters['boolArrayParam'][0], 'true')
      assert.deepStrictEqual(requester._parameters['boolArrayParam'][1], 'true')
      assert.deepStrictEqual(requester._parameters['boolArrayParam'][2], 'false')
    })
  }

}

runTest(PayloadAndParameterTest)
