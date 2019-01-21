self.addEventListener('install', function (event) {
    console.log("install")
});

self.addEventListener('activate', function (event) {
    console.log("activate")
});

self.addEventListener('activated', function (event) {
    console.log("activated")
});

let dictionary = null;

/**
 * @returns {Promise<any>}
 */
function getDictionary() {
    return new Promise((resolve, reject) => {
        if (dictionary) {
            resolve(dictionary);
        } else {
            caches
                .open("v1")
                .then(cache => {
                    console.log('    getDictionary/Cache is open');
                    return cache.match('images.black.json')
                })
                .then(response => {
                    console.log('    getDictionary/Cache result', response);
                    if (response) {
                        console.log('    getDictionary/Cache HIT');
                        // TODO
                    } else {
                        console.log('    getDictionary/Cache MISS');
                        fetch('images.black.json')
                            .then(response => {
                                console.log('    getDictionary/got json', response);
                                return response.json()
                            })
                            .then(value => {
                                console.log('    getDictionary/filled dictionary', value);
                                dictionary = value;
                                resolve(dictionary);
                            })
                            .catch(reason => {
                                reject(reason);
                            })
                    }
                })
                .catch(reason => {
                    reject(reason);
                })
        }
    })
}

/**
 * @param request
 * @returns {Promise<any | never>}
 */
function resolveTile(request) {
    console.log("resolveTile", request);

    let path = new URL(request.url).pathname;
    let imagePath = path.substr(7);
    console.log('resolveTile/Image path', imagePath);

    let result = getDictionary()
        .then(dictionary => {
            let ref = dictionary.images[imagePath];
            console.log('resolveTile/REF', ref);
            if (ref!==undefined) {
                let blackImagePath = dictionary.references[ref].path;
                console.log('resolveTile/BLACK_IMAGE', blackImagePath);

                return caches.open('v1')
                    .then(cache => {
                        console.log('resolveTile/cache open');
                        return cache.match(blackImagePath)
                            .then(response => {
                                console.log('resolveTile/cache_response', response);
                                if (response) {
                                    return response;
                                } else {
                                    return fetch(blackImagePath).then(response => {
                                        console.log('resolveTile/server_response', response);
                                        cache.put(blackImagePath, response.clone());
                                        return response;
                                    })
                                }
                            })
                    })
            } else {
                console.log('resolveTile/CONTENT_IMAGE', path);
                return fetch(path);
            }
        });
    console.log('resolveTile/result', result);
    return result;
}

self.addEventListener('fetch', function (event) {
    let path = new URL(event.request.url).pathname;
    console.log('---------------FETCH------------', path);

    if (path.startsWith('/tiles/')) {
        event.respondWith(resolveTile(event.request));
    } else {
        event.respondWith(fetch(event.request));
    }
});

// event.respondWith(
//     caches.match(event.request).then(function (response) {
//         return response || fetch(event.request);
//     })
// );

self.addEventListener('sync', function (event) {
    console.log("sync")
});

self.addEventListener('push', function (event) {
    console.log("push")
});