var express = require('express');
var router = express.Router();
var {auth}=require('../auth/jwt');
const { 
  novaPrijava
}=require('../controllers/prijave-controller');

/* GET users listing. */
router.post('/', auth, novaPrijava);

module.exports = router;
