var express = require('express');
var router = express.Router();
var {auth}=require('../auth/jwt');
const { 
  novaPrijava,
  aktivnePrijave
}=require('../controllers/prijave-controller');

/* GET users listing. */
router.post('/', auth, novaPrijava);
router.get('/', auth, aktivnePrijave);

module.exports = router;
