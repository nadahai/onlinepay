!
    function (U) {
        (function (e) {
            var t = !1,
                n = e.Promise;
            if (n) {
                var o = null,
                    r = null;
                try {
                    r = (o = n.resolve()).then
                } catch (e) { }
                o instanceof n && "function" == typeof r && !n.cast && (t = !0)
            }
            return t
        })(U) ||
        function () {
            var e, t;
            e = this,
                t = function () {
                    "use strict";
                    function s(e) {
                        return "function" == typeof e
                    }
                    var n = Array.isArray ? Array.isArray : function (e) {
                            return "[object Array]" === Object.prototype.toString.call(e)
                        },
                        o = 0,
                        t = void 0,
                        r = void 0,
                        u = function (e, t) {
                            d[o] = e,
                                d[o + 1] = t,
                            2 === (o += 2) && (r ? r(h) : b())
                        },
                        e = "undefined" != typeof window ? window : void 0,
                        a = e || {},
                        i = a.MutationObserver || a.WebKitMutationObserver,
                        c = void 0 === U && "undefined" != typeof process && "[object process]" === {}.toString.call(process),
                        l = "undefined" != typeof Uint8ClampedArray && "undefined" != typeof importScripts && "undefined" != typeof MessageChannel;
                    function f() {
                        var e = setTimeout;
                        return function () {
                            return e(h, 1)
                        }
                    }
                    var d = new Array(1e3);
                    function h() {
                        for (var e = 0; e < o; e += 2)(0, d[e])(d[e + 1]),
                            d[e] = void 0,
                            d[e + 1] = void 0;
                        o = 0
                    }
                    var p, m, v, g, b = void 0;
                    function y(e, t) {
                        var n = arguments,
                            o = this,
                            r = new this.constructor(P);
                        void 0 === r[w] && V(r);
                        var a, i = o._state;
                        return i ? (a = n[i - 1], u(function () {
                            return O(i, r, a, o._result)
                        })) : M(o, r, e, t),
                            r
                    }
                    function _(e) {
                        if (e && "object" == typeof e && e.constructor === this) return e;
                        var t = new this(P);
                        return E(t, e),
                            t
                    }
                    c ? b = function () {
                        return process.nextTick(h)
                    } : i ? (m = 0, v = new i(h), g = document.createTextNode(""), v.observe(g, {
                        characterData: !0
                    }), b = function () {
                        g.data = m = ++m % 2
                    }) : l ? ((p = new MessageChannel).port1.onmessage = h, b = function () {
                        return p.port2.postMessage(0)
                    }) : b = void 0 === e && "function" == typeof require ?
                        function () {
                            try {
                                var e = require("vertx");
                                return void 0 !== (t = e.runOnLoop || e.runOnContext) ?
                                    function () {
                                        t(h)
                                    } : f()
                            } catch (e) {
                                return f()
                            }
                        }() : f();
                    var w = Math.random().toString(36).substring(16);
                    function P() { }
                    var B = void 0,
                        A = 1,
                        C = 2,
                        S = new R;
                    function k(e) {
                        try {
                            return e.then
                        } catch (e) {
                            return S.error = e,
                                S
                        }
                    }
                    function T(e, t, n) {
                        var o, r, a, i;
                        t.constructor === e.constructor && n === y && t.constructor.resolve === _ ? (a = e, (i = t)._state === A ? I(a, i._result) : i._state === C ? x(a, i._result) : M(i, void 0,
                            function (e) {
                                return E(a, e)
                            },
                            function (e) {
                                return x(a, e)
                            })) : n === S ? (x(e, S.error), S.error = null) : void 0 === n ? I(e, t) : s(n) ? (o = t, r = n, u(function (t) {
                                var n = !1,
                                    e = function (e, t, n, o) {
                                        try {
                                            e.call(t, n, o)
                                        } catch (e) {
                                            return e
                                        }
                                    }(r, o,
                                        function (e) {
                                            n || (n = !0, o !== e ? E(t, e) : I(t, e))
                                        },
                                        function (e) {
                                            n || (n = !0, x(t, e))
                                        },
                                        t._label); !n && e && (n = !0, x(t, e))
                            },
                            e)) : I(e, t)
                    }
                    function E(e, t) {
                        var n;
                        e === t ? x(e, new TypeError("You cannot resolve a promise with itself")) : "function" == typeof (n = t) || "object" == typeof n && null !== n ? T(e, t, k(t)) : I(e, t)
                    }
                    function D(e) {
                        e._onerror && e._onerror(e._result),
                            L(e)
                    }
                    function I(e, t) {
                        e._state === B && (e._result = t, e._state = A, 0 !== e._subscribers.length && u(L, e))
                    }
                    function x(e, t) {
                        e._state === B && (e._state = C, e._result = t, u(D, e))
                    }
                    function M(e, t, n, o) {
                        var r = e._subscribers,
                            a = r.length;
                        e._onerror = null,
                            r[a] = t,
                            r[a + A] = n,
                            r[a + C] = o,
                        0 === a && e._state && u(L, e)
                    }
                    function L(e) {
                        var t = e._subscribers,
                            n = e._state;
                        if (0 !== t.length) {
                            for (var o = void 0,
                                     r = void 0,
                                     a = e._result,
                                     i = 0; i < t.length; i += 3) o = t[i],
                                r = t[i + n],
                                o ? O(n, o, r, a) : r(a);
                            e._subscribers.length = 0
                        }
                    }
                    function R() {
                        this.error = null
                    }
                    var N = new R;
                    function O(e, t, n, o) {
                        var r = s(n),
                            a = void 0,
                            i = void 0,
                            u = void 0,
                            c = void 0;
                        if (r) {
                            if ((a = function (e, t) {
                                try {
                                    return e(t)
                                } catch (e) {
                                    return N.error = e,
                                        N
                                }
                            }(n, o)) === N ? (c = !0, i = a.error, a.error = null) : u = !0, t === a) return void x(t, new TypeError("A promises callback cannot return that same promise."))
                        } else a = o,
                            u = !0;
                        t._state !== B || (r && u ? E(t, a) : c ? x(t, i) : e === A ? I(t, a) : e === C && x(t, a))
                    }
                    var F = 0;
                    function V(e) {
                        e[w] = F++ ,
                            e._state = void 0,
                            e._result = void 0,
                            e._subscribers = []
                    }
                    function j(e, t) {
                        this._instanceConstructor = e,
                            this.promise = new e(P),
                        this.promise[w] || V(this.promise),
                            n(t) ? (this._input = t, this.length = t.length, this._remaining = t.length, this._result = new Array(this.length), 0 === this.length ? I(this.promise, this._result) : (this.length = this.length || 0, this._enumerate(), 0 === this._remaining && I(this.promise, this._result))) : x(this.promise, new Error("Array Methods must be provided an Array"))
                    }
                    function z(e) {
                        this[w] = F++ ,
                            this._result = this._state = void 0,
                            this._subscribers = [],
                        P !== e && ("function" != typeof e &&
                        function () {
                            throw new TypeError("You must pass a resolver function as the first argument to the promise constructor")
                        }(), this instanceof z ?
                            function (t, e) {
                                try {
                                    e(function (e) {
                                            E(t, e)
                                        },
                                        function (e) {
                                            x(t, e)
                                        })
                                } catch (e) {
                                    x(t, e)
                                }
                            }(this, e) : function () {
                                throw new TypeError("Failed to construct 'Promise': Please use the 'new' operator, this object constructor cannot be called as a function.")
                            }())
                    }
                    return j.prototype._enumerate = function () {
                        for (var e = this.length,
                                 t = this._input,
                                 n = 0; this._state === B && n < e; n++) this._eachEntry(t[n], n)
                    },
                        j.prototype._eachEntry = function (t, e) {
                            var n = this._instanceConstructor,
                                o = n.resolve;
                            if (o === _) {
                                var r = k(t);
                                if (r === y && t._state !== B) this._settledAt(t._state, e, t._result);
                                else if ("function" != typeof r) this._remaining-- ,
                                    this._result[e] = t;
                                else if (n === z) {
                                    var a = new n(P);
                                    T(a, t, r),
                                        this._willSettleAt(a, e)
                                } else this._willSettleAt(new n(function (e) {
                                    return e(t)
                                }), e)
                            } else this._willSettleAt(o(t), e)
                        },
                        j.prototype._settledAt = function (e, t, n) {
                            var o = this.promise;
                            o._state === B && (this._remaining-- , e === C ? x(o, n) : this._result[t] = n),
                            0 === this._remaining && I(o, this._result)
                        },
                        j.prototype._willSettleAt = function (e, t) {
                            var n = this;
                            M(e, void 0,
                                function (e) {
                                    return n._settledAt(A, t, e)
                                },
                                function (e) {
                                    return n._settledAt(C, t, e)
                                })
                        },
                        z.all = function (e) {
                            return new j(this, e).promise
                        },
                        z.race = function (r) {
                            var a = this;
                            return n(r) ? new a(function (e, t) {
                                for (var n = r.length,
                                         o = 0; o < n; o++) a.resolve(r[o]).then(e, t)
                            }) : new a(function (e, t) {
                                return t(new TypeError("You must pass an array to race."))
                            })
                        },
                        z.resolve = _,
                        z.reject = function (e) {
                            var t = new this(P);
                            return x(t, e),
                                t
                        },
                        z._setScheduler = function (e) {
                            r = e
                        },
                        z._setAsap = function (e) {
                            u = e
                        },
                        z._asap = u,
                        z.prototype = {
                            constructor: z,
                            then: y,
                            catch: function (e) {
                                return this.then(null, e)
                            }
                        },
                        z.polyfill = function () {
                            var e = void 0;
                            if ("undefined" != typeof global) e = global;
                            else if (void 0 !== U) e = U;
                            else try {
                                    e = Function("return this")()
                                } catch (e) {
                                    throw new Error("polyfill failed because global object is unavailable in this environment")
                                }
                            var t = e.Promise;
                            if (t) {
                                var n = null;
                                try {
                                    n = Object.prototype.toString.call(t.resolve())
                                } catch (e) { }
                                if ("[object Promise]" === n && !t.cast) return
                            }
                            e.Promise = z
                        },
                        (z.Promise = z).polyfill(),
                        z
                },
                "object" == typeof exports && "undefined" != typeof module ? module.exports = t() : "function" == typeof define && define.amd ? define(t) : e.ES6Promise = t()
        }()
    }(self),
    function (r) {
        "use strict";
        var a, e, o = "AlipayJSBridge",
            v = r[o],
            t = navigator.userAgent || navigator.swuserAgent,
            n = "apm-h5",
            i = r.window,
            u = r.document,
            g = r.console,
            c = r.parseInt,
            s = [],
            l = {
                getBAPSI: {
                    isListening: !1,
                    lastState: 2,
                    on: function () {
                        l.getBAPSI.isListening || (v.call("startMonitorBackgroundAudio"), l.getBAPSI.isListening = !0, y.on("getBackgroundAudioPlayedStateInfo", l.getBAPSI.listener))
                    },
                    off: function () {
                        y.off("getBackgroundAudioPlayedStateInfo", l.getBAPSI.listener),
                            v.call("stopMonitorBackgroundAudio"),
                            l.getBAPSI.isListening = !1
                    },
                    listener: function (e) {
                        var t = (e.data || {}).status,
                            n = ["backgroundAudioPause", "backgroundAudioPlay", "backgroundAudioStop"][t];
                        n && t !== l.getBAPSI.lastState && (y.trigger(n), l.getBAPSI.lastState = t)
                    }
                }
            },
            b = {
                openBluetoothAdapter: {},
                closeBluetoothAdapter: {},
                getBluetoothAdapterState: {},
                startBluetoothDevicesDiscovery: {
                    b: function (e) {
                        return z(e._) && (e._ = [e._]),
                            S(e, {
                                _: "services"
                            }),
                            e
                    }
                },
                stopBluetoothDevicesDiscovery: {},
                getBluetoothDevices: {
                    b: function (e) {
                        return z(e._) && (e._ = [e._]),
                            S(e, {
                                _: "services"
                            }),
                            e
                    },
                    a: function (e) {
                        return q(e.devices) && L(e.devices,
                            function (e, t) {
                                S(t, {
                                    manufacturerData: "advertisData"
                                })
                            }),
                            e
                    }
                },
                getConnectedBluetoothDevices: {
                    a: function (e) {
                        return q(e.devices) && L(e.devices,
                            function (e, t) {
                                S(t, {
                                    manufacturerData: "advertisData"
                                })
                            }),
                            e
                    }
                },
                connectBLEDevice: {
                    b: function (e) {
                        return S(e, {
                            _: "deviceId"
                        }),
                            e
                    }
                },
                disconnectBLEDevice: {},
                writeBLECharacteristicValue: {},
                readBLECharacteristicValue: {},
                notifyBLECharacteristicValueChange: {},
                getBLEDeviceServices: {
                    b: function (e) {
                        return S(e, {
                            _: "deviceId"
                        }),
                            e
                    }
                },
                getBLEDeviceCharacteristics: {},
                onBLECharacteristicValueChange: {
                    m: "BLECharacteristicValueChange"
                },
                offBLECharacteristicValueChange: {
                    m: "BLECharacteristicValueChange"
                },
                onBluetoothAdapterStateChange: {},
                offBluetoothAdapterStateChange: {},
                onBLEConnectionStateChanged: {
                    m: "BLEConnectionStateChanged"
                },
                offBLEConnectionStateChanged: {
                    m: "BLEConnectionStateChanged"
                },
                onBluetoothDeviceFound: {
                    a: function (e) {
                        return S(e, {
                            manufacturerData: "advertisData"
                        })
                    }
                },
                offBluetoothDeviceFound: {},
                pushBizWindow: {},
                compressImage: {
                    b: function (e) {
                        return e.level = F(e.level) ? 4 : e.level,
                            S(e, {
                                _: "apFilePaths",
                                level: "compressLevel%d"
                            })
                    },
                    d: function (e, t) {
                        N() ? v.call("compressImage", e, t) : d(t, {
                            apFilePaths: e.apFilePaths || []
                        })
                    }
                },
                getLaunchParams: {
                    d: function (e, t) {
                        y.launchParams = i.ALIPAYH5STARTUPPARAMS || v.startupParams || {},
                        j(t) && t(y.launchParams)
                    }
                },
                onTabClick: {},
                offTabClick: {},
                onShare: {
                    m: "onShare"
                },
                offShare: {
                    m: "onShare"
                },
                connectSocket: {
                    b: function (e) {
                        return S(e, {
                            headers: "header"
                        })
                    }
                },
                sendSocketMessage: {
                    b: function (e) {
                        return S(e, {
                            _: "data"
                        })
                    }
                },
                closeSocket: {},
                onSocketOpen: {},
                offSocketOpen: {},
                onSocketMessage: {},
                offSocketMessage: {},
                onSocketError: {},
                offSocketError: {},
                onSocketClose: {},
                offSocketClose: {},
                alert: {
                    b: function (e) {
                        return F((e = S(e, {
                            _: "content",
                            content: "message%s",
                            buttonText: "button%s"
                        })).title) || (e.title = C("%s", e.title)),
                            e
                    }
                },
                confirm: {
                    b: function (e) {
                        return F((e = S(e, {
                            _: "content%s",
                            content: "message%s",
                            confirmButtonText: "okButton%s",
                            cancelButtonText: "cancelButton%s"
                        })).title) || (e.title = C("%s", e.title)),
                            e
                    },
                    a: function (e) {
                        return S(e, {
                            ok: "confirm"
                        })
                    }
                },
                showToast: {
                    m: "toast",
                    b: function (e) {
                        return S(e, {
                            _: "content%s"
                        }),
                        z(e.content) || (e.content = C("%s", e.content)),
                            e
                    }
                },
                hideToast: {},
                showLoading: {
                    b: function (e) {
                        return S(e, {
                            _: "content",
                            content: "text%s"
                        })
                    }
                },
                hideLoading: {},
                showNavigationBarLoading: {
                    m: "showTitleLoading"
                },
                hideNavigationBarLoading: {
                    m: "hideTitleLoading"
                },
                setNavigationBar: {
                    b: function (e) {
                        var t = "setTitle",
                            n = "setTitleColor",
                            o = "setBarBottomLineColor",
                            r = {
                                setTitle: {},
                                setTitleColor: {},
                                setBarBottomLineColor: {}
                            };
                        return r[t] = S(r[t], {
                                _: "title",
                                title: "title%s",
                                image: "image%b"
                            },
                            e),
                            r[n] = S(r[n], {
                                    backgroundColor: "color%c",
                                    reset: "reset"
                                },
                                e),
                            r[o] = S(r[o], {
                                    borderBottomColor: "color%c"
                                },
                                e),
                            r
                    },
                    d: function (e, t) {
                        var n = "setTitle",
                            o = "setTitleColor",
                            r = "setBarBottomLineColor",
                            a = {};
                        W(e[n]) || v.call(n, e[n]),
                        W(e[r]) || (v.call(r, e[r]), V(e[r].color) && (a.error = 2, a.errorMessage = "棰滆壊鍊间笉鍚堟硶")),
                            W(e[o]) ? d(t, a) : v.call(o, e[o],
                                function (e) {
                                    a = Q(e, a),
                                        t(a)
                                })
                    }
                },
                showTabBar: {
                    b: function (n) {
                        if (n.action = "create", n.activeIndex = n.activeIndex || 0, S(n, {
                            color: "textColor%c",
                            activeColor: "selectedColor%c",
                            activeIndex: "selectedIndex%d"
                        }), q(n.items)) {
                            var e = n.items;
                            n.items = [],
                                e.forEach(function (e, t) {
                                    (e = S(Q({},
                                        e), {
                                            title: "name%s",
                                            tag: "tag%s",
                                            icon: "icon%b",
                                            activeIcon: "activeIcon%b",
                                            badge: "redDot%s"
                                        },
                                        {
                                            tag: t,
                                            badge: F(e.badge) ? "-1" : e.badge
                                        })).icon = C("%b", e.icon),
                                        e.activeIcon = C("%b", e.activeIcon),
                                        n.items.push(e)
                                })
                        }
                        return n
                    },
                    d: function (e, n, t) {
                        var o = "showTabBar";
                        F(l.showTabBar) ? l.showTabBar = {
                            opt: t
                        } : g.error(o + " must be called at most once"),
                            y.on("tabClick",
                                function (e) {
                                    var t = {};
                                    S(t, {
                                            tag: "index%d"
                                        },
                                        {
                                            tag: U(e.data) && e.data.tag ? e.data.tag : "0"
                                        }),
                                        n(t)
                                }),
                            v.call("tabBar", e,
                                function (e) {
                                    A(o, e)
                                })
                    }
                },
                setTabBarBadge: {
                    m: "tabBar",
                    b: function (e) {
                        return e.action = "redDot",
                            S(e, {
                                    index: "tag%s",
                                    badge: "redDot%s"
                                },
                                {
                                    index: e.index
                                }),
                            e
                    }
                },
                showActionSheet: {
                    m: "actionSheet",
                    b: function (t) {
                        if (S(t, {
                            items: "btns",
                            cancelButtonText: "cancelBtn%s"
                        }), q(t.btns)) {
                            var e = t.btns;
                            t.btns = [],
                                e.forEach(function (e) {
                                    return t.btns.push(e + "")
                                })
                        }
                        return F(t.cancelBtn) && (t.cancelBtn = "鍙栨秷"),
                            t
                    },
                    a: function (e, t) {
                        return q(t.btns) && e.index === t.btns.length && (e.index = -1),
                            e
                    }
                },
                redirectTo: {
                    b: function (e) {
                        return S(e, {
                            _: "url"
                        }),
                        U(e.data) && (e.url = x(e.url, e.data)),
                            e
                    },
                    d: function (e) {
                        e.url && i.location.replace(e.url)
                    }
                },
                pushWindow: {
                    b: function (e) {
                        return S(e, {
                            _: "url",
                            params: "param"
                        }),
                        -1 < e.url.indexOf("?") && g.warn("try opt.data instead of querystring"),
                        -1 < e.url.indexOf("__webview_options__") && g.warn("try opt.params instead of __webview_options__"),
                        U(e.data) && (e.url = x(e.url, e.data), delete e.data),
                            e
                    }
                },
                popWindow: {
                    b: function (e) {
                        return U((e = h(e)).data) || (e.data = {
                            ___forResume___: e.data
                        }),
                            e
                    }
                },
                popTo: {
                    b: function (e) {
                        var t;
                        return S(e, {
                            _: (t = void 0, H(e._) && (t = "index"), z(e._) && (t = "urlPattern"), t)
                        }),
                        U(e.data) || (e.data = {
                            ___forResume___: e.data
                        }),
                            e
                    }
                },
                allowPullDownRefresh: {
                    d: function (e) {
                        var t = "onPullDownRefresh";
                        S(e, {
                            _: "allow"
                        }),
                            e.allow = !!F(e.allow) || !!e.allow,
                            U(l[t]) ? l[t].allow = e.allow : (l[t] = {
                                allow: e.allow
                            },
                                y.onPullDownRefresh()),
                            l[t].allow ? v.call("restorePullToRefresh") : l[t].event && l[t].event.preventDefault()
                    }
                },
                choosePhoneContact: {
                    m: "contact"
                },
                chooseAlipayContact: {
                    m: "chooseContact",
                    b: function (e) {
                        return S(e, {
                            _: "count"
                        }),
                        F(e.count) && (e.count = 1),
                            1 === e.count ? e.type = "single" : (e.type = "multi", e.count <= 0 || 10 < e.count ? e.multiMax = 10 : e.multiMax = e.count),
                            delete e.count,
                            e
                    },
                    a: function (e) {
                        return q(e.contacts) && e.contacts.forEach(function (e) {
                            S(e, {
                                headImageUrl: "avatar",
                                name: "realName"
                            }),
                                delete e.from
                        }),
                            e
                    }
                },
                share: {
                    b: function (e) {
                        var t = {},
                            n = {};
                        return t.onlySelectChannel = ["ALPContact", "ALPTimeLine", "ALPCommunity", "Weibo", "DingTalkSession", "SMS", "Weixin", "WeixinTimeLine", "QQ", "QQZone"],
                        M(e, "bizType") && (t.bizType = e.bizType),
                            delete (n = Q({},
                                e)).bizType,
                            delete n.onlySelectChannel,
                            S(n, {
                                image: "imageUrl"
                            }),
                            l.share = {
                                startShare: t,
                                shareToChannel: n
                            },
                            e
                    },
                    d: function (e, n) {
                        !1 === e.showToolBar && v.call("setToolbarMenu", {
                            menus: [],
                            override: !0
                        }),
                            v.call("startShare", l.share.startShare,
                                function (e) {
                                    var t = l.share.shareToChannel;
                                    e.channelName ? v.call("shareToChannel", {
                                            name: e.channelName,
                                            param: t
                                        },
                                        n) : n(e)
                                })
                    }
                },
                datePicker: {
                    b: function (e) {
                        switch (S(e, {
                            _: "formate",
                            formate: "mode",
                            currentDate: "beginDate",
                            startDate: "minDate",
                            endDate: "maxDate"
                        }), e.mode) {
                            case "HH:mm:ss":
                                e.mode = 0;
                                break;
                            case "yyyy-MM-dd":
                                e.mode = 1;
                                break;
                            case "yyyy-MM-dd HH:mm:ss":
                                e.mode = 2;
                                break;
                            default:
                                e.mode = 1
                        }
                        return e
                    },
                    a: function (e) {
                        return z(e.date) && (e.date = e.date.replace(/\//g, "-").trim()),
                            e
                    }
                },
                chooseCity: {
                    m: "getCities",
                    b: function (e) {
                        var t, n;
                        function o(e) {
                            var t;
                            return q(e) && (t = [], e.forEach(function (e) {
                                t.push(S({},
                                    {
                                        city: "name",
                                        adCode: "adcode%s",
                                        spell: "pinyin"
                                    },
                                    e))
                            }), e = t),
                                e
                        }
                        return S(e, {
                            showHotCities: "needHotCity",
                            cities: "customCities",
                            hotCities: "customHotCities"
                        }),
                            !0 === e.showLocatedCity ? (e.currentCity = "", e.adcode = "") : (delete e.currentCity, delete e.adcode),
                            delete e.showLocatedCity,
                            t = e.customCities,
                        F(e.customCities) || (e.customCities = o(t)),
                            n = e.customHotCities,
                        F(e.customHotCities) || (e.customHotCities = o(n)),
                            e
                    },
                    a: function (e) {
                        return S(e, {
                            adcode: "adCode"
                        }),
                            e
                    }
                },
                onBack: {
                    a: function (e) {
                        var t = {},
                            n = "onBack";
                        return U(l[n]) ? l[n].event = e : l[n] = {
                            event: e,
                            allowButton: !0
                        },
                        !1 === l[n].allowButton && e.preventDefault(),
                            t.backAvailable = l[n].allowButton,
                            t
                    },
                    e: {
                        handleEventData: !1
                    }
                },
                offBack: {},
                onResume: {
                    a: function (e) {
                        var t = {};
                        return F(e.data) || (t.data = e.data),
                        M(e.data, "___forResume___") && (t.data = e.data.___forResume___),
                            t
                    },
                    e: {
                        handleEventData: !1
                    }
                },
                offResume: {},
                onPause: {},
                offPause: {},
                onPageResume: {
                    a: function (e) {
                        var t = {};
                        return F(e.data) || (t.data = e.data),
                        M(e.data, "___forResume___") && (t.data = e.data.___forResume___),
                            t
                    },
                    e: {
                        handleEventData: !1
                    }
                },
                offPageResume: {},
                onPagePause: {},
                offPagePause: {},
                onTitleClick: {},
                offTitleClick: {},
                onPullDownRefresh: {
                    m: "firePullToRefresh",
                    a: function (e) {
                        var t = {},
                            n = "onPullDownRefresh";
                        return U(l[n]) ? l[n].event = e : l[n] = {
                            event: e,
                            allow: !0
                        },
                        !1 === l[n].allow && l[n].event.preventDefault(),
                            t.refreshAvailable = l[n].allow,
                            t
                    },
                    e: {
                        handleEventData: !1
                    }
                },
                offPullDownRefresh: {
                    m: "firePullToRefresh"
                },
                onNetworkChange: {
                    d: function (e, t, n, o) {
                        var r = function () {
                            return y.getNetworkType(t)
                        };
                        _("h5NetworkChange", o, r),
                            y.on("h5NetworkChange", r)
                    }
                },
                offNetworkChange: {
                    d: function (e, t, n, o) {
                        w("h5NetworkChange", o)
                    }
                },
                onAccelerometerChange: {
                    b: function () {
                        v.call("watchShake", {
                            monitorAccelerometer: !0
                        })
                    },
                    a: function (e) {
                        var t = {};
                        return S(t, {
                                x: "x",
                                y: "y",
                                z: "z"
                            },
                            U(e.data) ? e.data : e),
                            t
                    },
                    e: {
                        handleEventData: !1
                    }
                },
                offAccelerometerChange: {
                    b: function () {
                        v.call("watchShake", {
                            monitorAccelerometer: !1
                        })
                    }
                },
                onCompassChange: {
                    b: function () {
                        v.call("watchShake", {
                            monitorCompass: !0
                        })
                    },
                    a: function (e) {
                        var t = {};
                        return S(t, {
                                direction: "direction"
                            },
                            U(e.data) ? e.data : e),
                            t
                    },
                    e: {
                        handleEventData: !1
                    }
                },
                offCompassChange: {
                    b: function () {
                        v.call("watchShake", {
                            monitorCompass: !1
                        })
                    }
                },
                onBackgroundAudioPlay: {
                    b: function (e) {
                        return l.getBAPSI.on(),
                            e
                    }
                },
                offBackgroundAudioPlay: {},
                onBackgroundAudioPause: {
                    b: function (e) {
                        return l.getBAPSI.on(),
                            e
                    }
                },
                offBackgroundAudioPause: {},
                onBackgroundAudioStop: {
                    b: function (e) {
                        return l.getBAPSI.on(),
                            e
                    }
                },
                offBackgroundAudioStop: {},
                onAppResume: {},
                offAppResume: {},
                onAppPause: {},
                offAppPause: {},
                getNetworkType: {
                    a: function (e) {
                        return F(e.networkInfo) || (e.networkType = function (e) {
                            z(e) && (e = e.toUpperCase());
                            return e
                        }(e.networkInfo)),
                            delete e.err_msg,
                            delete e.networkInfo,
                            e
                    }
                },
                scan: {
                    b: function (e) {
                        return S(e, {
                            _: "type"
                        }),
                            e.type = e.type || "qr",
                            e
                    },
                    a: function (e) {
                        return (e.qrCode || e.barCode) && (e.code = e.qrCode || e.barCode, delete e.qrCode, delete e.barCode),
                            e
                    }
                },
                watchShake: {
                    b: function (e) {
                        return W(e) && (e = null),
                            e
                    }
                },
                getLocation: {
                    b: function (e) {
                        return S(e, {
                            accuracy: "horizontalAccuracy",
                            type: "requestType%d"
                        }),
                        F(e.requestType) && (e.requestType = 2),
                        N() && (F(e.isHighAccuracy) && (e.isHighAccuracy = !0), F(e.isNeedSpeed) && (e.isNeedSpeed = !0)),
                            e
                    },
                    a: function (e) {
                        return S(e, {
                            citycode: "cityCode",
                            adcode: "adCode"
                        }),
                        F(e.city) && e.province && (e.city = e.province),
                        e.latitude && (e.latitude = C("%s", e.latitude)),
                        e.longitude && (e.longitude = C("%s", e.longitude)),
                        e.accuracy && (e.accuracy = C("%f", e.accuracy)),
                        e.speed && (e.speed = C("%f", e.speed)),
                            e
                    }
                },
                getSystemInfo: {
                    a: function (e) {
                        var t = "pixelRatio",
                            n = "windowWidth",
                            o = "windowHeight",
                            r = "language";
                        if (!M(e, "error")) {
                            e[t] = C("%f", e[t]),
                                e[n] = C("%d", e[n]),
                                e[r] = (e[r] || "").replace(/\s?\w+\/((?:\w|-)+)$/, "$1"),
                                e[o] = C("%d", e[o]);
                            try {
                                O() && y.compareVersion("10.0.12") < 0 && (e[o] = i.screen.height - 64)
                            } catch (e) { }
                        }
                        return e
                    }
                },
                vibrate: {},
                getServerTime: {},
                previewImage: {
                    m: "imageViewer",
                    b: function (t) {
                        return S(t, {
                            _: "urls",
                            current: "init%d"
                        }),
                        F(t.init) && (t.init = 0),
                            t.images = [],
                            (t.urls || []).forEach(function (e) {
                                t.images.push({
                                    u: e
                                })
                            }),
                            delete t.urls,
                            t
                    }
                },
                chooseImage: {
                    b: function (e) {
                        return S(e, {
                            _: "count%d"
                        }),
                        F(e.count) && (e.count = 1),
                        z(e.sourceType) && (e.sourceType = [e.sourceType]),
                            e
                    },
                    a: function (e) {
                        return S(e, {
                            errorCode: "error",
                            errorDesc: "errorMessage",
                            localIds: "apFilePaths",
                            tempFilePaths: "apFilePaths"
                        }),
                            delete e.scene,
                            delete e.localIds,
                            delete e.tempFilePaths,
                        z(e.apFilePaths) && (e.apFilePaths = R(e.apFilePaths)),
                            e
                    }
                },
                chooseVideo: {
                    b: function (e) {
                        return S(e, {
                            _: "maxDuration%d"
                        }),
                        z(e.sourceType) && (e.sourceType = [e.sourceType]),
                        z(e.camera) && (e.camera = [e.camera]),
                            e
                    },
                    a: function (e) {
                        switch (S(e, {
                            errorCode: "error",
                            errorDesc: "errorMessage",
                            msg: "errorMessage",
                            localId: "apFilePath",
                            tempFilePath: "apFilePath",
                            tempFile: "apFilePath"
                        }), delete e.localId, delete e.tempFilePath, delete e.tempFile, e.error) {
                            case 0:
                                delete e.error;
                                break;
                            case 1:
                                e.error = 2;
                                break;
                            case 2:
                                e.error = 10;
                                break;
                            case 3:
                                e.error = 11;
                                break;
                            case 4:
                                e.error = 12
                        }
                        return e
                    }
                },
                uploadFile: {
                    b: function (e) {
                        var t;
                        return S(e, {
                            headers: "header",
                            fileName: "name",
                            fileType: "type"
                        }),
                            t = e.filePath,
                        /^[a-z0-9|]+$/i.test(t) && (e.localId = e.filePath, delete e.filePath),
                            e
                    },
                    a: function (e) {
                        return 2 === e.error && (e.error = 11),
                            e
                    }
                },
                saveImage: {
                    b: function (e, t) {
                        return S(e, {
                            _: "url",
                            url: "src"
                        }),
                        j(t) && (e.cusHandleResult = !0),
                            e
                    }
                },
                downloadFile: {
                    b: function (e) {
                        return S(e, {
                            headers: "header"
                        }),
                            e
                    },
                    a: function (e) {
                        return S(e, {
                            tempFilePath: "apFilePath",
                            errorCode: "error"
                        }),
                            delete e.tempFilePath,
                            e
                    }
                },
                setSessionData: {
                    b: function (n) {
                        return U((n = h(n)).data) || (n.data = {
                            data: n.data
                        }),
                            L(n.data,
                                function (e, t) {
                                    n.data[e] = JSON.stringify(t)
                                }),
                            n
                    }
                },
                getSessionData: {
                    b: function (e) {
                        return z(e._) && (e.keys = [e._]),
                        q(e._) && (e.keys = e._),
                            delete e._,
                            e
                    },
                    a: function (n) {
                        return L(n.data,
                            function (e, t) {
                                n.data[e] = R(t)
                            }),
                            n
                    }
                },
                startBizService: {
                    b: function (e) {
                        return S(e, {
                            _: "name",
                            params: "param%s"
                        }),
                            e
                    }
                },
                tradePay: {
                    b: function (e) {
                        return S(e, {
                            _: "orderStr"
                        }),
                            e
                    }
                },
                getAuthCode: {
                    b: function (e) {
                        return S(e, {
                            _: "scopes"
                        }),
                            z(e.scopes) ? e.scopeNicks = [e.scopes] : q(e.scopes) ? e.scopeNicks = e.scopes : e.scopeNicks = ["auth_base"],
                            delete e.scopes,
                            e
                    },
                    a: function (e) {
                        return S(e, {
                            authcode: "authCode"
                        }),
                            e
                    }
                },
                getAuthUserInfo: {
                    a: function (e) {
                        return S(e, {
                            nick: "nickName",
                            userAvatar: "avatar"
                        }),
                            e
                    }
                },
                openInBrowser: {
                    b: function (e) {
                        return S(e, {
                            _: "url"
                        })
                    }
                },
                openLocation: {
                    b: function (e) {
                        return F(e.scale) && (e.scale = 15),
                            e
                    }
                },
                showPopMenu: {
                    b: function (n) {
                        if (S(n, {
                            _: "items",
                            items: "menus"
                        }), U(l.showPopMenu) ? l.showPopMenu.menus = {} : l.showPopMenu = {
                            menus: {}
                        },
                            q(n.menus)) {
                            var e = n.menus;
                            n.menus = [],
                                e.forEach(function (e, t) {
                                    z(e) && (e = {
                                        title: e
                                    }),
                                    F((e = S(Q({},
                                        e), {
                                            title: "name%s",
                                            tag: "tag%s",
                                            badge: "redDot%s"
                                        },
                                        {
                                            tag: t,
                                            title: e.title,
                                            badge: F(e.badge) ? "-1" : e.badge
                                        })).icon) || (e.icon = C("%b", e.icon)),
                                        n.menus.push(e),
                                        l.showPopMenu.menus[e.name] = t
                                })
                        }
                        return n
                    },
                    d: function (e, n) {
                        var t = "showPopMenu"; !0 !== l.showPopMenu.onEvent && (l.showPopMenu.onEvent = !0, y.on("popMenuClick",
                            function (e) {
                                var t = {};
                                S(t, {
                                        title: "index%d"
                                    },
                                    {
                                        title: U(e.data) && e.data.title ? l.showPopMenu.menus[e.data.title] : "-1"
                                    }),
                                    n(t)
                            })),
                            v.call(t, e,
                                function (e) {
                                    A(t, e)
                                })
                    }
                },
                setOptionButton: {
                    m: "setOptionMenu",
                    b: function (n) {
                        if (z(n._) && (n.title = n._, delete n._), q(n._) && (n.items = n._, delete n._), S(n, {
                            items: "menus",
                            type: "iconType",
                            badge: "redDot%s"
                        }), F(n.icon) || (n.icon = C("%b", n.icon)), U(l.setOptionButton) ? l.setOptionButton.menus = [] : l.setOptionButton = {
                            menus: []
                        },
                            q(n.menus)) {
                            var o = n.menus;
                            n.menus = [],
                                o.forEach(function (e, t) {
                                    F((e = S(Q({},
                                        e), {
                                            type: "icontype",
                                            badge: "redDot%s"
                                        },
                                        {
                                            badge: F(e.badge) ? "-1" : e.badge
                                        })).icon) || (e.icon = C("%b", e.icon)),
                                        n.menus.unshift(e),
                                        l.setOptionButton.menus[o.length - 1 - t] = t
                                }),
                            0 < n.menus.length && F(n.override) && (n.override = !0)
                        }
                        if (j(l.setOptionButton.onEvent) && y.off("optionMenu", l.setOptionButton.onEvent), j(n.onClick)) {
                            var r = n.onClick,
                                e = function (e) {
                                    var t = 0,
                                        n = {};
                                    U(e.data) && H(e.data.index) && 0 < l.setOptionButton.menus.length && (t = l.setOptionButton.menus[e.data.index]),
                                        n.index = C("%d", t),
                                        r(n)
                                };
                            l.setOptionButton.onEvent = e,
                            !0 !== n.reset && y.on("optionMenu", e),
                                delete n.onClick
                        }
                        return n
                    },
                    d: function (e, t) {
                        v.call("setOptionMenu", e, t),
                        O() && d(t, {}),
                            y.showOptionButton()
                    }
                },
                showOptionButton: {
                    m: "showOptionMenu"
                },
                hideOptionButton: {
                    m: "hideOptionMenu"
                },
                showBackButton: {},
                hideBackButton: {},
                allowBack: {
                    d: function (e) {
                        var t = "onBack";
                        S(e, {
                            _: "allowButton"
                        }),
                            e.allowButton = !!F(e.allowButton) || !!e.allowButton,
                        "boolean" == typeof e.allowGesture && v.call("setGestureBack", {
                            val: e.allowGesture
                        }),
                            U(l[t]) ? l[t].allowButton = e.allowButton : (l[t] = {
                                allowButton: e.allowButton
                            },
                                y.onBack()),
                        !1 === e.allowButton && l[t].event && l[t].event.preventDefault()
                    }
                },
                startRecord: {
                    m: "startAudioRecord",
                    b: function (e) {
                        return S(e, {
                                maxDuration: "maxRecordTime%f",
                                minDuration: "minRecordTime%f",
                                bizType: "business"
                            },
                            {
                                maxDuration: e.maxDuration || 60,
                                minDuration: e.minDuration || 1
                            }),
                        F(e.business) && (e.business = n),
                            e
                    },
                    a: function (e) {
                        return S(e, {
                            tempFilePath: "apFilePath",
                            identifier: "apFilePath"
                        }),
                            e
                    }
                },
                stopRecord: {
                    m: "stopAudioRecord"
                },
                cancelRecord: {
                    m: "cancelAudioRecord"
                },
                playVoice: {
                    m: "startPlayAudio",
                    b: function (e) {
                        return S(e, {
                            _: "filePath",
                            filePath: "identifier",
                            bizType: "business"
                        }),
                        F(e.business) && (e.business = n),
                            e
                    },
                    a: function (e) {
                        return S(e, {
                            identifier: "filePath"
                        }),
                            e
                    }
                },
                pauseVoice: {
                    m: "pauseAudioPlay"
                },
                resumeVoice: {
                    m: "resumeAudioPlay"
                },
                stopVoice: {
                    m: "stopAudioPlay"
                },
                makePhoneCall: {
                    d: function (e, t) {
                        var n = "tel:";
                        S(e, {
                            _: "number"
                        }),
                            n += e.number,
                            v.call("openInBrowser", {
                                    url: n
                                },
                                t)
                    }
                },
                playBackgroundAudio: {
                    b: function (e) {
                        return S(e, {
                                _: "url",
                                url: "audioDataUrl%s",
                                title: "audioName%s",
                                singer: "singerName%s",
                                describe: "audioDescribe%s",
                                logo: "audioLogoUrl%s",
                                cover: "coverImgUrl%s",
                                bizType: "business"
                            },
                            {
                                bizType: e.bizType || n
                            }),
                            e
                    },
                    a: function (e) {
                        return S(e, {
                            describe: "errorMessage"
                        }),
                            m(e, 12, 0),
                            e
                    }
                },
                pauseBackgroundAudio: {
                    a: function (e) {
                        return S(e, {
                            describe: "errorMessage"
                        }),
                            m(e, 12, 0),
                            e
                    }
                },
                stopBackgroundAudio: {
                    a: function (e) {
                        return S(e, {
                            describe: "errorMessage"
                        }),
                            m(e, 12, 0),
                            e
                    }
                },
                seekBackgroundAudio: {
                    b: function (e) {
                        return S(e, {
                                _: "position",
                                bizType: "business"
                            },
                            {
                                bizType: e.bizType || n
                            }),
                            e.position = C("%f", e.position),
                            e
                    },
                    a: function (e) {
                        return S(e, {
                            describe: "errorMessage"
                        }),
                            m(e, 12, 0),
                            e
                    }
                },
                getBackgroundAudioPlayerState: {
                    a: function (e) {
                        return S(e, {
                            audioDataUrl: "url",
                            describe: "errorMessage"
                        }),
                            m(e, 12, 0),
                            e
                    }
                }
            },
            y = {
                version: "3.1.1",
                ua: t,
                isAlipay: E(/AlipayClient/),
                alipayVersion: (e = t.match(/AlipayClient[a-zA-Z]*\/(\d+(?:\.\d+)+)/), e && e.length ? e[1] : ""),
                compareVersion: function (e) {
                    var t = y.alipayVersion.split(".");
                    e = e.split(".");
                    for (var n, o, r = 0; r < t.length; r++) {
                        if (n = c(e[r], 10) || 0, (o = c(t[r], 10) || 0) < n) return - 1;
                        if (n < o) return 1
                    }
                    return 0
                },
                parseQueryString: function (e) {
                    var t, n = {},
                        o = e || i.location.search,
                        r = {
                            true: !0,
                            false: !1
                        };
                    o = (o = 0 === o.indexOf("?") ? o.substr(1) : o) ? o.split("&") : "";
                    for (var a = 0; a < o.length; a++)(t = o[a].split("="))[1] = decodeURIComponent(t[1]),
                        t[1] = F(r[t[1]]) ? t[1] : r[t[1]],
                        n[t[0]] = t[1];
                    return k("parseQueryString"),
                        n
                },
                enableDebug: function () {
                    y.debug = !0
                },
                on: function (e, t) {
                    var n = "ready" === e;
                    n || "back" === e ? u.addEventListener(n ? o + "Ready" : e, t, !1) : (e = e.replace(/ready/, o + "Ready")).split(/\s+/g).forEach(function (e) {
                        u.addEventListener(e, t, !1)
                    })
                },
                off: function (e, t) {
                    u.removeEventListener(e, t, !1)
                },
                trigger: function (e, t) {
                    var n = u.createEvent("Events");
                    return n.initEvent(e, !1, !0),
                        n.data = t || {},
                        u.dispatchEvent(n),
                        n
                },
                ready: function (t) {
                    if (I()) return new Promise(e);
                    function e(e) {
                        f() ? (j(t) && t(), j(e) && e()) : y.on("ready",
                            function () {
                                f(),
                                j(t) && t(),
                                j(e) && e()
                            })
                    }
                    e()
                },
                call: function () {
                    var m = $(arguments);
                    if (I()) return y.ready().then(function () {
                        return new Promise(e)
                    });
                    function e(n, o) {
                        var r, a, i, u, c, e, s, t, l, f, d, h, p;
                        r = m[0] + "",
                            a = m[1],
                        F(i = m[2]) && j(a) && (i = a, a = {}),
                        !U(a) && 2 <= m.length && (a = {
                            _: a
                        }),
                        F(a) && (a = {}),
                            u = function (e, t, n) {
                                var o = b[e],
                                    r = o && o.b ? o.b(Q({},
                                        t), n) : t,
                                    a = B(e, "optionModifier");
                                if (j(a)) {
                                    var i = a(r, n);
                                    U(i) && (r = i)
                                }
                                return r
                            }(r, a, i),
                            c = function (e, t) {
                                var n = {};
                                j((t = t || {}).success) && (n.success = t.success, delete t.success);
                                j(t.fail) && (n.fail = t.fail, delete t.fail);
                                j(t.complete) && (n.complete = t.complete, delete t.complete);
                                return n
                            }(0, u),
                        F(u) && g.error("please confirm " + r + ".before() returns the options."),
                            l = !(!(d = b[r]) || !d.d) && d.d,
                            f = M(a, "_") ? a._ : a,
                            T(r, f, u),
                            s = P("on", r),
                            t = P("off", r),
                            e = function (e) {
                                var t = void 0;
                                e = e || {},
                                s && !1 !== B(r, "handleEventData") && (t = function (e) {
                                    var t = {};
                                    F(e.data) || (t = U(t = e.data) ? t : {
                                        data: t
                                    });
                                    return t
                                }(e)),
                                F(t = function (e, t, n, o, r) {
                                    var a = b[e],
                                        i = a && a.a ? a.a((c = t, "[object Event]" === J(c) ? t : Q({},
                                            t)), n, o, r) : Q({},
                                            t),
                                        u = B(e, "resultModifier");
                                    var c;
                                    if (j(u)) {
                                        var s = u(i, n, o, r);
                                        U(s) && (i = s)
                                    }
                                    return i
                                }(r, t || e, u, a, i)) && g.error("please confirm " + r + ".after() returns the result."),
                                    t = A(r, t),
                                    T(r, f, u, e, t),
                                    M(t, "error") || M(t, "errorMessage") ? (j(o) && o(t), j(c.fail) && c.fail(t)) : (j(n) && n(t), j(c.success) && c.success(t)),
                                j(c.complete) && c.complete(t),
                                j(i) && i(t)
                            },
                            j(l) ? l(u, e, a, i) : s ? (_(s, i, e, c), y.on(s, e)) : t ? w(t, i) : v.call((p = b[h = r]) && p.m ? p.m : h, u, e),
                            k(r)
                    }
                    f() ? e() : s.push(m)
                },
                extendJSAPI: function (o, r) {
                    !r && z(o) && (o = [o]),
                        L(o,
                            function (e) {
                                var t = e;
                                if (!0 !== r) {
                                    var n = o[t];
                                    j(n) && (n = {
                                        doing: n
                                    }),
                                    z(n) && (t = n, (n = {})[t] = {}),
                                        b[t] = S(b[t] || {},
                                            {
                                                mapping: "m",
                                                before: "b",
                                                doing: "d",
                                                after: "a"
                                            },
                                            n),
                                    U(n.extra) && (b[t].e = b[t].e || {},
                                        b[t].e = Q(b[t].e, n.extra))
                                }
                                y[t] = function () {
                                    return y.call.apply(null, [t].concat($(arguments)))
                                }
                            },
                            !0)
                }
            };
        function f() {
            return (v = v || r[o]) && v.call
        }
        function d(e, t) {
            setTimeout(function () {
                    e(t)
                },
                1)
        }
        function h(e, t) {
            var n = !1;
            return t = t || "data",
                M(e, "_") ? (e[t] = e._, delete e._) : (L(e,
                    function (e) {
                        e !== t && (n = !0)
                    }), n && (n = e, (e = {})[t] = n)),
                e
        }
        function p(e, n, t) {
            var o, r = !1;
            return F(e) || (l.EVENTS || (l.EVENTS = {}), l.EVENTS[e] || (l.EVENTS[e] = {
                callbacks: []
            }), l.EVENTS[e].callbacks || (l.EVENTS[e].callbacks = []), l.EVENTS[e].callbacks.forEach(function (e, t) {
                e.cb === n && (r = e, o = t)
            }), t && H(o) && l.EVENTS[e].callbacks.splice(o, 1)),
                r
        }
        function _(e, t, n, o) {
            p(e, t) || l.EVENTS[e].callbacks.push({
                cb: t,
                _cb: n,
                _cbSFC: o
            })
        }
        function w(t, e) {
            var n = p(t, e, !0);
            j(e) ? n && y.off(t, n._cb) : (l.EVENTS[t].callbacks.forEach(function (e) {
                y.off(t, e._cb)
            }), l.EVENTS[t].callbacks = [])
        }
        function P(e, t) {
            var n = b[t],
                o = !1,
                r = "off" === e ? /^off([A-Z])(\w+)/ : /^on([A-Z])(\w+)/;
            return n && r.test(t) && (t = t.match(r), !(o = n.m) && t[1] && t[2] && (o = function (e) {
                z(e) && (e = e.toLowerCase());
                return e
            }(t[1]) + t[2])),
                o
        }
        function B(e, t) {
            var n = b[e] || {};
            return (n.e || n.extra || {})[t]
        }
        function A(e, t) {
            return M(t, "error") && (t.error = c(t.error, 10)),
            !1 !== B(e, "handleResultSuccess") && m(t),
            0 === t.error && (delete t.error, delete t.errorMessage),
            0 < t.error && t.error < 10 && g.error(e, t),
                t
        }
        function m(e, t, n) {
            return n = !F(n) && n,
            M(e, "error") || e.success !== n || (e.error = H(t) ? t : 2),
                delete e.success,
                e
        }
        function C(e, t) {
            return "%s" === e && (t = function (e) {
                var t = e;
                if (U(e) || q(e)) try {
                    t = JSON.stringify(e)
                } catch (e) { } else t = e + "";
                return t
            }(t)),
            "%c" === e && (t = function (e) {
                var t = "" + e;
                0 === t.indexOf("#") && (t = t.substr(1));
                3 === t.length && (t = t.replace(/(.)/g, "$1$1"));
                V(t = c(t, 16)) && g.error(e + " is invalid hex color.");
                return t
            }(t)),
            "%b" === e && (t = function (e) {
                z(e) && (e = e.replace(/^data:(\/|\w|\-|\.)+;base64,/i, ""));
                return e
            }(t)),
            "%d" === e && (t = c(t, 10)),
            "%f" === e && (t = parseFloat(t)),
                t
        }
        function S(a, i, u) {
            var c;
            return u = u || {},
                L(i,
                    function (e, t) {
                        var n, o, r;
                        n = i[e],
                            o = (n || "").match(/(\w+)(%\w)$/i),
                            r = {
                                k: n
                            },
                        o && (r.k = o[1], r.t = o[2]),
                        !F(t = (c = r).k) && (M(a, e) || M(u, e)) && F(a[t]) && (a[t] = C(c.t, F(u[e]) ? a[e] : u[e]), t !== e && delete a[e])
                    }),
                a
        }
        y.extendJSAPI.mapping = S,
            y.extendJSAPI.toType = C,
        y.isAlipay || g.warn("Run alipayjsapi.js in Alipay please!"),
            y.extendJSAPI(b, !0),
            y.on("ready",
                function () {
                    s.length &&
                    function t() {
                        D(function () {
                            var e = s.shift();
                            y.call.apply(null, e),
                            s.length && t()
                        })
                    }()
                });
        var k = function () {
            var t = [],
                n = void 0,
                o = !1;
            function r() {
                setTimeout(function () {
                        if (0 < t.length) {
                            var e = t.join("|");
                            y.ready(function () {
                                v.call("remoteLog", {
                                    type: "monitor",
                                    bizType: "ALIPAYJSAPI",
                                    logLevel: 1,
                                    actionId: "MonitorReport",
                                    seedId: "ALIPAYJSAPI_INVOKE_COUNTER",
                                    param1: e
                                })
                            }),
                            y.debug && g.info("REMOTE_LOG_QUEUE>", t),
                                t = []
                        } !F(n) && clearTimeout(n),
                            o = !1
                    },
                    0)
            }
            return y.on("back",
                function () {
                    r()
                }),
                function (e) {
                    t.push(e),
                        6 <= t.length ? r() : o || (o = !0, n = setTimeout(function () {
                                r()
                            },
                            5e3))
                }
        }();
        function T() {
            var e, t, n, o, r, a, i = $(arguments);
            y.debug && (e = i[0], t = i[1], n = i[2], o = i[3], r = i[4], a = [3 < i.length ? "RETURN>" : "INVOKE>", e, M(t, "_") ? t._ : t, n], 3 < i.length && a.push(o), 4 < i.length && a.push(r), g.info(a))
        }
        function E(e) {
            return e.test(t)
        }
        var D = i.requestAnimationFrame || i.webkitRequestAnimationFrame || i.mozRequestAnimationFrame || i.msRequestAnimationFrame ||
            function (e, t) {
                i.setTimeout(function () {
                        e(+ new Date, t)
                    },
                    1e3 / 60)
            };
        function I() {
            if (void 0 === a) {
                var e = !1,
                    t = r.Promise;
                if (t) {
                    var n = null,
                        o = null;
                    try {
                        o = (n = t.resolve()).then
                    } catch (e) { }
                    n instanceof t && j(o) && !t.cast && (e = !0)
                }
                e || g.warn("try callback since no Promise detected"),
                    a = e
            }
            return a
        }
        function x(e, t) {
            var n, o = t;
            return U(t) && (n = [], L(t,
                function (e, t) {
                    n.push(e + "=" + encodeURIComponent(F(t) ? "" : t))
                }), o = n = n.join("&")),
                /\?/.test(e) ? /&$/.test(e) || /\?$/.test(e) || (o = "&" + o) : o = "?" + o,
            e + o
        }
        function M(e, t) {
            return !(!U(e) && !q(e)) && e.hasOwnProperty(t)
        }
        function L(e, t, n) {
            var o, r, a;
            if (n || (!(a = e) || j(a) || !q(a) && !H(a.length))) {
                for (r in e) if (!1 === t(r, e[r])) return e
            } else for (o = 0; o < e.length; o++) if (!1 === t(o, e[o])) return e;
            return e
        }
        function R(t) {
            try {
                t = JSON.parse(t)
            } catch (e) {
                g.warn(e, t)
            }
            return t
        }
        function N() {
            return E(/android/i)
        }
        function O() {
            return E(/iPad|iPod|iPhone|iOS/i)
        }
        function F(e) {
            return "[object Undefined]" === J(e)
        }
        function V(e) {
            return "NaN" === c(e, 10).toString()
        }
        function j(e) {
            return "[object Function]" === J(e)
        }
        function z(e) {
            return "string" == typeof e
        }
        function U(e) {
            return "[object Object]" === J(e)
        }
        function H(e) {
            return "[object Number]" === J(e)
        }
        function q(e) {
            return "[object Array]" === J(e)
        }
        function J(e) {
            return Object.prototype.toString.call(e)
        }
        function W(e) {
            for (var t in e) return !1;
            return !0
        }
        function $(e) {
            for (var t = 1 < arguments.length && void 0 !== arguments[1] ? arguments[1] : 0, n = e.length - t, o = new Array(n), r = 0; r < n; r++) o[r] = e[r + t];
            return o
        }
        function Q(e) {
            var t, n, o = $(arguments, 1);
            if (!U(e)) return e;
            for (var r = 0,
                     a = o.length; r < a; r++) for (n in t = o[r]) hasOwnProperty.call(t, n) && (e[n] = t[n]);
            return e
        }
        r._AP = y,
            "undefined" != typeof module && module.exports ? module.exports = y : "function" == typeof define && (define.amd || define.cmd) ? define(function () {
                return y
            }) : r.ap = r.AP = y
    }(self);