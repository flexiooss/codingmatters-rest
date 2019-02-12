class FakeHttpRequester {
    headers = {};
    parameters = {};
    nextBody = "{}";

    get(){
        this.lastMethod = "get";
        return new FakeResponseDelegate(null, this.parameter );
    }

    post( contentType, body ){
        this.lastMethod = "post";
        return new FakeResponseDelegate(body, this.parameter );
    }

    put( contentType, body ){
        this.lastMethod = "put";
        return new FakeResponseDelegate(body, this.parameter );
    }

    patch( contentType, body ){
        this.lastMethod = "patch";
        return new FakeResponseDelegate(body, this.parameter );
    }

    delete( contentType, body ){
        this.lastMethod = "delete";
        return new FakeResponseDelegate(body, this.parameter );
    }

    head( contentType, body ){
        this.lastMethod = "head";
        return new FakeResponseDelegate(body, this.parameter );
    }

    arrayParameter( name, values ){
        this.parameters[name] = values;
    }

    parameter( name, values ){
        this.parameters[name] = values;
    }

    arrayHeader( name, values ){
        this.headers[name] = values;
    }

    header( name, values ){
        this.headers[name] = values;
    }

    path( path ){
        this.path = path;
    }
}

