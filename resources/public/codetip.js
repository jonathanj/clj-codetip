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


function initialisePaste(readOnly, contentType) {
    var node = document.getElementById('content');
    window.editor = CodeMirror.fromTextArea(node, {
        lineNumbers: true,
        theme: "solarized dark",
        autofocus: true,
        readOnly: !!readOnly,
    });
    window.editor.getWrapperElement().className += ' mousetrap';
    if (contentType === undefined) {
        contentType = document.getElementById('content-type').value;
    }
    if (contentType !== undefined) {
        setEditorMode(window.editor, contentType);
    }
}


function setEditorMode(editor, contentType) {
    var mode = CodeMirror.findModeByMIME(contentType);
    if (mode !== undefined) {
        editor.setOption('mode', mode.mime);
        CodeMirror.autoLoadMode(editor, mode.mode);
    }
}


function modeInputChanged(node) {
    setEditorMode(window.editor, node.value);
}


function savePaste() {
    document.getElementById('form').submit();
}

function newPaste() {
    window.location = '/';
}


_addEventListener('load', function () {
    Mousetrap.reset();
    Mousetrap.bindGlobal('mod+s', function(evt) {
        savePaste();
        return false;
    });

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

    var target = document.getElementById('focus');
    if (target !== null) {
        _addEventListener('click', function (evt) {
            if (evt.target !== target) {
                return false;
            }
            window.editor.focus();
            return false;
        }, target);
    }
});


