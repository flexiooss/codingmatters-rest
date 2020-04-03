class Blob {

    constructor(content, options){
        this._content = content;
        this._options = options;
    }

    content() {
        return this._content;
    }

    options() {
        return this._options;
    }
}

class FileReader{
    constructor(){
        this.result = null;
    }

    readAsText( blob ){
        this.result = blob.content();
        this.onloadend();
    }
}

export {Blob, FileReader};
