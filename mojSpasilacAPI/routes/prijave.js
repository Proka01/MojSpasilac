var express = require('express');
var router = express.Router();
var {auth}=require('../auth/jwt');
const { 
  novaPrijava,
  aktivnePrijave,
  lokacijaZaSpasioca
}=require('../controllers/prijave-controller');

/* GET users listing. */
router.post('/', auth, novaPrijava);
router.get('/', auth, aktivnePrijave);
router.get('/zaSpasioca', auth, lokacijaZaSpasioca);

module.exports = router;
