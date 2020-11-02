'use strict';
'use struct';

function HashMap(){
     /** Map 大小 **/
     var size = 0;
     /** 对象 **/
     var entry = new Object();

     /** 存 **/
     this.put = function (key , value)
     {
         if(!this.containsKey(key))
         {
             size ++ ;
         }
         entry[key] = value;
     }

     /** 取 **/
     this.get = function (key)
     {
         if( this.containsKey(key) )
         {
             return entry[key];
         }
         else
         {
             return null;
         }
     }

     /** 删除 **/
     this.remove = function ( key )
     {
         if( delete entry[key] )
         {
             size --;
         }
     }

     /** 是否包含 Key **/
     this.containsKey = function ( key )
     {
         return (key in entry);
     }

     /** 是否包含 Value **/
     this.containsValue = function ( value )
     {
         for(var prop in entry)
         {
             if(entry[prop] == value)
             {
                 return true;
             }
         }
         return false;
     }

     /** 所有 Value **/
     this.values = function ()
     {
         var values = new Array(size);
         for(var prop in entry)
         {
             values.push(entry[prop]);
         }
         return values;
     }

     /** 所有 Key **/
     this.keys = function ()
     {
         var keys = new Array(size);
         for(var prop in entry)
         {
             keys.push(prop);
         }
         return keys;
     }

     /** Map Size **/
     this.size = function ()
     {
         return size;
     }
 }

var RE = {
	currentRange: {
		startContainer: null,
		startOffset: 0,
		endContainer: null,
		endOffset: 0
	},
	cache: {
		editor: null,
		title: null,
		currentLink: null,
		line: null
	},
	commandSet: ['bold', 'italic', 'strikethrough', 'redo', 'undo'],
	schemeCache: {
		FOCUS_SCHEME: 'focus://',
		CHANGE_SCHEME: 'change://',
		STATE_SCHEME: 'state://',
		CALLBACK_SCHEME: 'callback://',
		IMAGE_SCHEME: 'image://'
	},
	setting: {
		screenWidth: 0,
		margin: 20
	},
	titleLimit:{
        txtNote:null,//文本框
        txtLimit:null,//提示字数的input
        limitCount:20,//限制的字数
	},

	imageCache: new HashMap(),
	init: function init() {
		//初始化内部变量
		var _self = this;
		_self.initCache();
		_self.initSetting();
		_self.initLimit();
		_self.bind();
		_self.focus();
        console.log("init:::" + document.documentElement.outerHTML);
	},

	bind: function bind() {
		var _self = this;

		var _self$schemeCache = _self.schemeCache,
		    FOCUS_SCHEME = _self$schemeCache.FOCUS_SCHEME,
		    STATE_SCHEME = _self$schemeCache.STATE_SCHEME,
		    CALLBACK_SCHEME = _self$schemeCache.CALLBACK_SCHEME;
		document.addEventListener('selectionchange', _self.saveRange, false);

		_self.cache.title.addEventListener('focus', function () {
			AndroidInterface.setViewEnabled(true);
		}, false);

		_self.cache.title.addEventListener('blur', function () {
			AndroidInterface.setViewEnabled(false);
		}, false);

		_self.cache.editor.addEventListener('blur', function () {
			_self.saveRange();
		}, false);

		_self.cache.editor.addEventListener('click', function (evt) {
			_self.saveRange();
			_self.getEditItem(evt);
		}, false);

		document.addEventListener('click', function (evt) {
            _self.onClick();
        }, false);

		_self.cache.editor.addEventListener('keyup', function (evt) {
			if (evt.which == 37 || evt.which == 39 || evt.which == 13 || evt.which == 8) {
				_self.getEditItem(evt);
			}
		}, false);

		_self.cache.editor.addEventListener('input', function () {
		    var markdownWords = _self.trim(_self.markdownWords(),"g");
			AndroidInterface.setHtmlContent(_self.getTitle().length , markdownWords.length);
		}, false);

		_self.titleLimit.txtNote.addEventListener('input', function () {
			_self.wordsLimit();
		}, false);

		_self.cache.title.addEventListener('input', function () {
			var content=_self.markdownWords();
			AndroidInterface.setHtmlContent(_self.getTitle().length , content.length);
		}, false);
	},

	initCache: function initCache() {
		var _self = this;
		_self.cache.editor = document.getElementById('editor');
		_self.cache.title = document.getElementById('title');
		_self.cache.line = document.getElementsByClassName('line')[0];
		_self.cache.editor.style.minHeight = window.innerHeight - 69 + 'px';
	},
	initSetting: function initSetting() {
		var _self = this;
		_self.setting.screenWidth = window.innerWidth - 20;
	},
	initLimit: function initLimit(){
	    var _self = this;
	    _self.titleLimit.txtNote = document.getElementById("title");
	    _self.titleLimit.txtLimit = document.getElementById("txtCount");
	    // 屏蔽了标题的回车输入
	    _self.titleLimit.txtNote.onkeypress = function(){
	        var cursor = _self.getCursortPosition(_self.titleLimit.txtNote);
	        if(event.keyCode == 32 && cursor == 0){
	            // 空格，部分手机上得不到这个事件
	            return false;
	        }
            if (event.keyCode == 13 ){
                return false;
            }

        }
	},
	focus: function focus() {
		//聚焦
		var _self = this;
		var range = document.createRange();
		range.selectNodeContents(this.cache.title);
		range.collapse(false);
		var select = window.getSelection();
		select.removeAllRanges();
		select.addRange(range);
		_self.cache.title.focus();
	},
	getHtml: function getHtml() {
		var _self = this;
		return _self.cache.editor.innerHTML;
	},
	noMarkdownWords: function noMarkdownWords() {
		var _self = this;
		var content = _self.cache.editor.innerHTML.replace(/<(?!\/?a)\/?[^>]*>|\u56FE\u7247\u4E0A\u4F20\u5931\u8D25\uFF0C\u8BF7\u70B9\u51FB\u91CD\u8BD5|\s+/g, '');
        AndroidInterface.noMarkdownWords(content);
		return content;
	},
	markdownWords: function markdownWords() {
        var _self = this;
//        var content = _self.cache.editor.innerHTML.replace(/<div\\s+\\S+>\\s+\\S+<\/div>|<[divimginput]+ class=".*">|\u56FE\u7247\u4E0A\u4F20\u5931\u8D25\uFF0C\u8BF7\u70B9\u51FB\u91CD\u8BD5/g, '').replace(/\n|\t/g,'').replace(/<div>[u4e00-u9fa5]+<\/div>/g,"").replace(/div|span/g,'p').trim();
        // 由于 js 正则不支持前瞻后顾 ，内容返回出去后 由 Java 处理
        var content = _self.cache.editor.innerHTML;
        AndroidInterface.markdownWords(content);
        return content;
    },

    changeTitlePlaceholder: function changeTitlePlaceholder(value){
        this.cache.title.setAttribute("title-placeholder", value);
    },

    changeContentPlaceholder: function changeContentPlaceholder(value){
        this.cache.editor.setAttribute("editor-placeholder", value);
    },

    getTitle: function getTitle() {
        var _self = this;
        var title = _self.cache.title.innerHTML.replace(/<br>/g,'').replace(/&nbsp;/g," ").replace(/(^\s*)|(\s*$)/g, "");
        return title;
    },

    resultWords: function resultWords(boolean) {
        var _self = this;
        var value = boolean;
        console.log("isPublish:::" + value);
        AndroidInterface.resultWords(_self.getTitle(),_self.markdownWords(),_self.noMarkdownWords(),document.documentElement.outerHTML,value);
    },

    // 暂时没用到
    restoreDraft: function restoreDraft(title,content){
        var _self = this;
        _self.cache.title.innerHTML = title;
    },

	saveRange: function saveRange() {
		//保存节点位置
		var _self = this;
		var selection = window.getSelection();
		if (selection.rangeCount > 0) {
			var range = selection.getRangeAt(0);
			var startContainer = range.startContainer,
			    startOffset = range.startOffset,
			    endContainer = range.endContainer,
			    endOffset = range.endOffset;

			_self.currentRange = {
				startContainer: startContainer,
				startOffset: startOffset,
				endContainer: endContainer,
				endOffset: endOffset
			};
		}
	},
	reduceRange: function reduceRange() {
		//还原节点位置
		var _self = this;
		var _self$currentRange = _self.currentRange,
		    startContainer = _self$currentRange.startContainer,
		    startOffset = _self$currentRange.startOffset,
		    endContainer = _self$currentRange.endContainer,
		    endOffset = _self$currentRange.endOffset;

		var range = document.createRange();
		var selection = window.getSelection();
		selection.removeAllRanges();
		range.setStart(startContainer, startOffset);
		range.setEnd(endContainer, endOffset);
		selection.addRange(range);
	},
	exec: function exec(command) {
		//执行指令
		var _self = this;
		if (_self.commandSet.indexOf(command) !== -1) {
		    console.log("exec:::" + command);
			document.execCommand(command, false, null);
			if(_self.commandSet.indexOf(command) == 3){
			    // 滚动到光标
                _self.cache.editor.blur();
                _self.cache.editor.focus();
			}
			console.log("execCommand:::" + document.documentElement.outerHTML);
		} else {
			var value = '<' + command + '>';
			document.execCommand('formatBlock', false, value);
			_self.getEditItem({});
		}
	},

	onClick: function onClick(){
	    AndroidInterface.onClick();
	},

	// 同步状态
	getEditItem: function getEditItem(evt) {
		//通过点击时，去获得一个当前位置的所有状态
		var _self = this;
		var _self$schemeCache2 = _self.schemeCache,
		    STATE_SCHEME = _self$schemeCache2.STATE_SCHEME,
		    CHANGE_SCHEME = _self$schemeCache2.CHANGE_SCHEME,
		    IMAGE_SCHEME = _self$schemeCache2.IMAGE_SCHEME;
		if (evt.target && evt.target.tagName === 'A') {
			_self.cache.currentLink = evt.target;
			var name = evt.target.innerText;
			var href = evt.target.getAttribute('href');
			window.location.href = CHANGE_SCHEME + encodeURI(name + '@_@' + href);
		} else {
			if (evt.which == 8) {
				AndroidInterface.noMarkdownWords(_self.noMarkdownWords());
			}
			var items = [];
			_self.commandSet.forEach(function (item) {
				if (document.queryCommandState(item)) {
				    console.log("queryCommandState:::" + item);
					items.push(item);
				}
			});
			console.log("getEditItem:::" + document.documentElement.outerHTML);
			if (document.queryCommandValue('formatBlock')) {
				items.push(document.queryCommandValue('formatBlock'));
			}
			window.location.href = STATE_SCHEME + encodeURI(items.join(','));
		}
	},
	insertHtml: function insertHtml(html) {
		var _self = this;
		document.execCommand('insertHtml', false, html);
	},

	insertHtmlDIV: function insertHtmlDIV(html) {
	    console.log("insertHtmlDIV:::" + html);
        document.getElementById('editor').innerHTML=html;
    },

	setBackgroundColor: function setBackgroundColor(color) {
	    var _self = this;
	    document.body.style.backgroundColor = color;
	},
	setFontColor: function setFontColor(color) {
        document.body.style.color = color;
	},
	setLineColor: function setLineColor(color) {
	    var _self = this;
	    _self.cache.editor.style.borderColor = color;
	},

	// 插入分割线
	insertLine: function insertLine() {
		var _self = this;
		var html = '<hr><div><br></div>';
		_self.insertHtml(html);
		_self.getEditItem({});
		window.scrollTo(0,document.body.scrollHeight);
	},

	// 插入链接
	insertLink: function insertLink(name, url) {
		var _self = this;
		var html = '<a href="' + url + '" class="editor-link">' + name + '</a>';
		_self.insertHtml(html);
	},

	// 修改链接
	changeLink: function changeLink(name, url) {
		var _self = this;
		var current = _self.cache.currentLink;
		var len = name.length;
		current.innerText = name;
		current.setAttribute('href', url);
		var selection = window.getSelection();
		var range = selection.getRangeAt(0).cloneRange();
		var _self$currentRange2 = _self.currentRange,
		    startContainer = _self$currentRange2.startContainer,
		    endContainer = _self$currentRange2.endContainer;

		selection.removeAllRanges();
		range.setStart(startContainer, len);
		range.setEnd(endContainer, len);
		selection.addRange(range);
	},

	pareseHtml: function pareseHtml(title,content){
	    var _self = this;
        _self.cache.title.innerHTML = title;
	    var image = '<div><br></div><div class="block">\n\t\t\t\t<div class="img-block"><div style="width: 100% " class="process">\n\t\t\t\t\t<div class="fill">\n\t\t\t\t\t</div>\n\t\t\t\t</div>\n\t\t\t\t<img class="images" data-id="' + id + '" style="width: 100% ; height: auto ;" src="' + url + '"/>\n\t\t\t\t<div class="cover" style="width: 100% ; height: auto "></div>\n\t\t\t\t<div class="delete">\n\t\t\t\t\t<img class="error" src="./reload.png">\n\t\t\t\t\t<div class="tips">\u56FE\u7247\u4E0A\u4F20\u5931\u8D25\uFF0C\u8BF7\u70B9\u51FB\u91CD\u8BD5</div>\n\t\t\t\t\t<div class="markdown"></div>\n\t\t\t\t</div></div>\n\t\t\t\t<input class="dec" type="text" placeholder="\u8BF7\u8F93\u5165\u56FE\u7247\u540D\u5B57">\n\t\t\t</div><div><br></div>';

	},

	// 插入图片
	insertImage: function insertImage(url, id, width, height) {
		var _self = this;
		var newWidth = 0,
		    newHeight = 0;
		var screenWidth = _self.setting.screenWidth;

		if (width > screenWidth) {
			newWidth = screenWidth;
			newHeight = height * newWidth / width;
		} else {
			newWidth = width;
			newHeight = height;
		}
		var image = '<div><br></div><div class="block">\n\t\t\t\t<div class="img-block"><div style="width: ' + newWidth + 'px" class="process">\n\t\t\t\t\t<div class="fill">\n\t\t\t\t\t</div>\n\t\t\t\t</div>\n\t\t\t\t<img class="images" data-id="' + id + '" style="width: ' + newWidth + 'px; height: ' + newHeight + 'px;" src="' + url + '"/>\n\t\t\t\t<div class="cover" style="width: ' + newWidth + 'px; height: ' + newHeight + 'px"></div>\n\t\t\t\t<div class="delete">\n\t\t\t\t\t<img class="error" src="./reload.png">\n\t\t\t\t\t<div class="tips">\u56FE\u7247\u4E0A\u4F20\u5931\u8D25\uFF0C\u8BF7\u70B9\u51FB\u91CD\u8BD5</div>\n\t\t\t\t\t<div class="markdown"></div>\n\t\t\t\t</div></div>\n\t\t\t\t<input class="dec" type="text" placeholder="\u8BF7\u8F93\u5165\u56FE\u7247\u540D\u5B57">\n\t\t\t</div><div><br></div>';
		if( width * height <= 0 ){
		    image = '<div><br></div><div class="block">\n\t\t\t\t<div class="img-block"><div style="width: 100% " class="process">\n\t\t\t\t\t<div class="fill">\n\t\t\t\t\t</div>\n\t\t\t\t</div>\n\t\t\t\t<img class="images" data-id="' + id + '" style="width: 100% ; height: auto ;" src="' + url + '"/>\n\t\t\t\t<div class="cover" style="width: 100% ; height: auto "></div>\n\t\t\t\t<div class="delete">\n\t\t\t\t\t<img class="error" src="./reload.png">\n\t\t\t\t\t<div class="tips">\u56FE\u7247\u4E0A\u4F20\u5931\u8D25\uFF0C\u8BF7\u70B9\u51FB\u91CD\u8BD5</div>\n\t\t\t\t\t<div class="markdown"></div>\n\t\t\t\t</div></div>\n\t\t\t\t<input class="dec" type="text" placeholder="\u8BF7\u8F93\u5165\u56FE\u7247\u540D\u5B57">\n\t\t\t</div><div><br></div>';
		}
		_self.insertHtml(image);
		var img = document.querySelector('img[data-id="' + id + '"]');
		var imgBlock = img.parentNode;
		imgBlock.parentNode.contentEditable = false;
		imgBlock.addEventListener('click', function (e) {
			e.stopPropagation();
			var current = e.currentTarget;
			var img = current.querySelector('.images');
			var id = img.getAttribute('data-id');
			window.location.href = _self.schemeCache.IMAGE_SCHEME + encodeURI(id);
		}, false);
        imgBlock.parentNode.addEventListener('DOMNodeRemovedFromDocument', function (evt) {
            evt.stopPropagation();
            var current = evt.currentTarget;
            var img = current.querySelector('.images');
            var id = img.getAttribute('data-id');
            AndroidInterface.deleteImage(id);
        }, false);
		_self.imageCache.put(id, imgBlock.parentNode);
		window.scrollTo(0,document.body.scrollHeight);
	},

	addImageClickListener: function addImageClickListener(ids){
        var _self = this;
        console.log("addImageClickListener:::" + ids);
        var array = ids.split(',');

        for(var item in array) {
            var img = document.querySelector('img[data-id="' + array[item] + '"]');
            if(img != null){
                console.log("addImageClick:::" + array[item]);
                var imgBlock = img.parentNode;

                imgBlock.parentNode.contentEditable = false;
                imgBlock.addEventListener('click', function (e) {
                    e.stopPropagation();
                    var current = e.currentTarget;
                    var img = current.querySelector('.images');
                    var id = img.getAttribute('data-id');
                    window.location.href = _self.schemeCache.IMAGE_SCHEME + encodeURI(id);
                }, false);
                imgBlock.parentNode.addEventListener('DOMNodeRemovedFromDocument', function (evt) {
                    evt.stopPropagation();
                    var current = evt.currentTarget;
                    var img = current.querySelector('.images');
                    var id = img.getAttribute('data-id');
                    AndroidInterface.deleteImage(id);
                }, false);

                if(imgBlock.parentNode != null){
                    var dec = imgBlock.parentNode.querySelector('.dec');
                    var markdown = imgBlock.parentNode.querySelector('.markdown');
                    dec.addEventListener('input', function () {
                        console.log("--------------------"+markdown.innerHTML);
                        var imageMarkdown = markdown.innerHTML;
                        var reg = /@!\[.*?]\((\d+)\)/
                        markdown.innerHTML="@!["+dec.value+"]("+imageMarkdown.match(reg)[1]+")"
                    }, false);
                }
                _self.imageCache.put(array[item], imgBlock.parentNode);
            }
        }
	},

	// 图片上传进度
	changeProcess: function changeProcess(id, process,imageId) {
		var _self = this;
		var block = _self.imageCache.get(id);
		if(block == null){
		    return;
		}
		var fill = block.querySelector('.fill');
		fill.style.width = process + '%';
		if (process == 100) {
			var cover = block.querySelector('.cover');
			var dec = block.querySelector('.dec');
			var markdown = block.querySelector('.markdown');
			var process = block.querySelector('.process');
			var imgBlock = block.querySelector('.img-block');
			dec.addEventListener('input', function () {
                markdown.innerHTML="@!["+dec.value+"]("+imageId+")"
            }, false);

            if(markdown.innerHTML==""){
                markdown.innerHTML="@![image]("+imageId+")"
            }
			imgBlock.removeChild(cover);
			imgBlock.removeChild(process);
		}
	},

	hideTitle: function hideTitle(){
	    var _self = this;
	    console.log("hideTitle:::");
	    _self.cache.title.style.display="none";
	    _self.cache.line.style.display="none";
	    document.getElementById("content").style.paddingTop="10px";
	    _self.cache.editor.focus();
	},

	// 限制标题输入字数
    wordsLimit: function wordsLimit(){
        var _self = this;
        console.log("title.innerHTML::"+_self.cache.title.innerHTML);
        var title = _self.cache.title.innerHTML.replace(/<br>/g,'').replace(/&nbsp;/g," ");
        var cursor = _self.getCursortPosition(_self.cache.title);
        if(cursor == 1){
            _self.cache.title.innerHTML = title.replace(/(^\s*)/g, "");
            var el = _self.cache.title;
            var sel = window.getSelection();
            sel.collapse(el, 1);
            el.focus();
        }
        title = title.replace(/(^\s*)|(\s*$)/g, "");
        title = title.replace(/&amp;/g,"&");
        var limitCount = _self.titleLimit.limitCount;
        if(title.length < 10 ){
            document.getElementById("stay").style.display="none";
        }
        if(title.length >=10 ){
            document.getElementById("stay").style.display="block";
        }

        if(title.length > limitCount ){
            _self.cache.title.innerHTML = title.substring(0,limitCount).replace(/(^\s*)|(\s*$)/g, "");
            title = _self.cache.title.innerHTML;
            _self.focus();
            if(title.length < 10 ){
               document.getElementById("stay").style.display="none";
            }

        }
        _self.titleLimit.txtLimit.innerText = title.length;
    },

    // 删除图片
	removeImage: function removeImage(id) {
		var _self = this;
		var block = _self.imageCache.get(id);
		block.parentNode.removeChild(block);
		_self.imageCache.remove(id);
	},

	// 图片上传失败
	uploadFailure: function uploadFailure(id) {
		var _self = this;
		var block = _self.imageCache.get(id);
		var del = block.querySelector('.delete');
		del.style.display = 'block';
	},

	// 图片上传后重传
	uploadReload: function uploadReload(id) {
		var _self = this;
		var block = _self.imageCache.get(id);
		var del = block.querySelector('.delete');
		del.style.display = 'none';
	},

	stopBubble: function stopBubble(e) {
        //如果提供了事件对象，则这是一个非IE浏览器
        if ( e && e.stopPropagation ){
            e.stopPropagation(); //因此它支持W3C的stopPropagation()方法
        }else{
            window.event.cancelBubble = true; //否则，我们需要使用IE的方式来取消事件冒泡
        }
     },

     // 获取当前光标位置
     getCursortPosition: function getCursortPosition(element){
        var caretOffset = 0;
        var doc = element.ownerDocument || element.document;
        var win = doc.defaultView || doc.parentWindow;
        var sel;
        if (typeof win.getSelection != "undefined") {//谷歌、火狐
            sel = win.getSelection();
            if (sel.rangeCount > 0) {//选中的区域
                var range = win.getSelection().getRangeAt(0);
                var preCaretRange = range.cloneRange();//克隆一个选中区域
                preCaretRange.selectNodeContents(element);//设置选中区域的节点内容为当前节点
                preCaretRange.setEnd(range.endContainer, range.endOffset);  //重置选中区域的结束位置
                caretOffset = preCaretRange.toString().length;
            }
        } else if ((sel = doc.selection) && sel.type != "Control") {//IE
            var textRange = sel.createRange();
            var preCaretTextRange = doc.body.createTextRange();
            preCaretTextRange.moveToElementText(element);
            preCaretTextRange.setEndPoint("EndToEnd", textRange);
            caretOffset = preCaretTextRange.text.length;
         }
        return caretOffset;
     },

    trim: function trim(str,is_global){
        var result;
        str = str.replace(/&nbsp;/g,"");
        result = str.replace(/(^\s+)|(\s+$)/g,"");
        if(is_global.toLowerCase()=="g"){
            result = result.replace(/\s/g,"");
        }
        console.log("trim::"+result);
        return result;
    }

};
RE.init();

