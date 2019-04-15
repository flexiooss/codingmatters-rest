class FakeHttpRequester {

    constructor(){
        this._responseHeaders = {};
        this._responseHeaders["Content-Type"] = "Shit";
        this._headers = {};
        this._parameters = {};
        this._nextCode = 200;
        this._nextBody = "{}";
        this._path = null;
        this._lastMethod = null;
    }

    get(){
        this._lastMethod = "get";
        return new FakeResponseDelegate( this._nextCode, null, this._responseHeaders );
    }

    delete(){
        this._lastMethod = "delete";
        return new FakeResponseDelegate( this._nextCode, null, this._responseHeaders );
    }

    head(){
        this._lastMethod = "head";
        return new FakeResponseDelegate( this._nextCode, null, this._responseHeaders );
    }

    post( contentType, body ){
        this._lastMethod = "post";
        this._lastBody = body;
        this._lastContentType = contentType;
        return new FakeResponseDelegate( this._nextCode, this._nextBody, this._responseHeaders );
    }

    put( contentType, body ){
        this._lastMethod = "put";
        return new FakeResponseDelegate( this._nextCode, this._nextBody, this._responseHeaders );
    }

    patch( contentType, body ){
        this._lastMethod = "patch";
        return new FakeResponseDelegate( this._nextCode, _nextBody, this._responseHeaders );
    }

    arrayParameter( name, values ){
        this._parameters[name] = values;
    }

    parameter( name, values ){
        this._parameters[name] = values;
    }

    arrayHeader( name, values ){
        this._headers[name] = values;
    }

    header( name, values ){
        this._headers[name] = values;
    }

    path( path ){
        this._path = path;
    }

    nextBody( body ){
        this._nextBody = body;
    }

    lastBody(){
        return this._lastBody;
    }
}

class FakeResponseDelegate {

    constructor( code, body, headers ){
        this._body = body;
        this._headers = headers;
        this._code = code;
    }

    header( name ){
        if( name in this._headers ){
            return this._headers[name];
        }else{
            return null
        }
    }

    code(){
        return this._code;
    }

    payload(){
        return this._body;
    }

}

export {FakeHttpRequester, FakeResponseDelegate};