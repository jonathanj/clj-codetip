/**
 * Cross-browser best-attempt implementation of `addEventListener`.
 */
function _addEventListener(eventName, handler, target) {
    var target = target || window;
    if (target.addEventListener !== undefined) {
        return target.addEventListener(eventName, function(evt) {
            var ret = handler.apply(this, arguments);
            if (ret === false) {
                evt.stopPropagation();
                evt.preventDefault();
            }
            return ret;
        }, false);
    } else if (target.attachEvent !== undefined) {
        return target.attachEvent('on' + eventName, function() {
            window.event.target = window.event.srcElement;
            var ret = handler.call(target, window.event);
            if (ret === false) {
                window.event.returnValue = false;
                window.event.cancelBubble = true;
            }
            return ret;
        });
    }
}


_addEventListener('load', function () {
    Mousetrap.reset();
    var target = document.getElementById('focus');
    if (target !== null) {
        _addEventListener('click', function (event) {
            if (event.target !== target) {
                return false;
            }
            window.editor.focus();
            return false;
        }, target);
    }
});


function _initialiseBasicPasteView() {
    window.editor = CodeMirror.fromTextArea(
        document.getElementById('content'),
        {'lineNumbers': true,
         'lineWrapping': true,
         'theme': 'solarized dark',
         'autofocus': true,
         'dragDrop': false});
}


/**
 * Navigate to the paste creation location.
 */
function newPaste() {
    window.location = '/';
}


/**
 * Initialise the read-only paste view.
 *
 * @param contentType: Content-type to initialise the editor's mode to.
 */
function initialisePasteView(contentType) {
    _initialiseBasicPasteView();
    window.editor.setOption('readOnly', true);
    if (contentType !== undefined && contentType !== null) {
        setEditorMode(contentType);
    }
    _addEventListener('load', function () {
        Mousetrap.bindGlobal('mod+p', function(evt) {
            newPaste();
            return false;
        });

        /*
        Mousetrap.bindGlobal('mod+r', function(evt) {
            console.log('raw paste');
            return false;
        });
        */
    });
}


/**
 * Save the current paste.
 */
function savePaste() {
    document.getElementById('form').submit();
}


/**
 * Initialise the paste creation view.
 */
function initialiseNewPaste() {
    _initialiseBasicPasteView();
    window.editor.setOption('readOnly', false);
    var dropNode = document.getElementById('focus');
    _addEventListener('dragover', triggerDragDropzone, dropNode);
    var modeNode = document.getElementById('content-type');
    _addEventListener('change', function (event) {
        setEditorMode(event.target.value);
    }, modeNode);
    _addEventListener('load', function () {
        Mousetrap.bindGlobal('mod+s', function(evt) {
            savePaste();
            return false;
        });
    });
}


/**
 * Set the editor mode by MIME type.
 */
function setEditorMode(contentType) {
    var mode = CodeMirror.findModeByMIME(contentType);
    if (mode !== undefined) {
        window.editor.setOption('mode', mode.mime);
        CodeMirror.autoLoadMode(window.editor, mode.mode);
    }
}


/**
 * Create the drag dropzone that accepts file drops.
 */
function createDropzone() {
    var node = document.getElementById('dropzone');
    if (node !== null) {
        return node;
    }

    node = crel('div', {'id': 'dropzone',
                        'class': 'dropzone-zone'},
                crel('div', {'class': 'dropzone'},
                     crel('div', {'class': 'dropzone-text'},
                          'Drop files here')));
    document.body.appendChild(node);
    _addEventListener('dragover', function () { return false }, node);
    _addEventListener('drop', itemDropped, node);
    return node;
}


/**
 * Destroy the existing dropzone.
 */
function destroyDropzone() {
    var node = document.getElementById('dropzone');
    if (node !== null) {
        document.body.removeChild(node);
    }
}


/**
 * Handle the initial drag event and create the dropzone.
 */
function triggerDragDropzone(event) {
    var types = Array.prototype.slice.call(event.dataTransfer.types, 0),
        files = event.dataTransfer.files || [];
    if (files.length > 0 || types.indexOf('Files') !== -1) {
        createDropzone();
        return false;
    }
}


/**
 * Guess the editor mode from a `File`.
 */
function guessEditorModeFromFile(f) {
    var mode;
    if (mode == null && f.type) {
        // XXX: Try normalise MIME type first.
        mode = CodeMirror.findModeByMIME(f.type);
    }
    if (mode == null && f.name) {
        var parts = f.name.split('.');
        if (parts.length > 1) {
            mode = CodeMirror.findModeByExtension(parts[parts.length -1]);
        }
    }
    if (mode == null) {
        mode = CodeMirror.findModeByMIME('text/plain');
    }
    return mode;
}


/**
 * Place the content of a dropped file into the editor and guess the mode.
 */
function itemDropped(event) {
    destroyDropzone();
    var files = event.dataTransfer.files || [];
    if (files.length > 0) {
        var reader = new FileReader();
        reader.onload = function (e) {
            var mode = guessEditorModeFromFile(files.item(0));
            window.editor.setValue(e.target.result);
            if (mode !== null) {
                document.getElementById('content-type').value = mode.mime;
                setEditorMode(mode.mime);
            }
        };
        reader.readAsText(files.item(0));
        return false;
    }
}
