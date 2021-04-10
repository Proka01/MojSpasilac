var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.use('/korisnici', require('./../routes/korisnici'));
router.use('/prijave', require('./../routes/prijave'));

module.exports = router;
