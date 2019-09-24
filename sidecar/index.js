const bodyParser = require('body-parser');
const express = require('express');
const cookieSession = require('cookie-session');
const proxy = require('http-proxy-stream');

const PROXY_URL = process.env.SIDECAR_PROXY_URL || "http://localhost:8080";
const PORT = process.env.SIDECAR_PORT || 3030;

/*
 * Configure express
 */
const app = express();

app.set('trust proxy', 1);
app.set('view engine', 'pug');

app.use(bodyParser.urlencoded({ extended: true }));

app.use(cookieSession({
    name: 'session',
    keys: ['lorem-ipsum']
}));

/*
 * Routes of application
 */
app.post('/_auth', function (req, res) {
    req.session.username = req.body.username;
    res.redirect(req.body.redirect);
});

app.use('/_auth/logout', function (req, res) {
    req.session = null;
    res.render('goodbye');
});

app.use('/_auth', express.static('public'));

app.use(function (req, res) {
    let forward = false;

    if (req.session.username) {
        forward = true;
        req.headers['x-user-id'] = req.session.username;
    } else if (req.headers['x-user-id']) {
        forward = true;
    }

    if (forward) {
        proxy(req, { url: `${PROXY_URL}${req.originalUrl}` }, res);
    } else {
        res.render('login', { redirect: req.originalUrl });
    }
});

/*
 * Start server
 */
app.listen(PORT, function () {
    console.log('Example app listening on port ' + PORT + '!');
});