var express = require('express');
var router = express.Router();
var {auth}=require('../auth/jwt');
const { 
  novaPrijava,
  aktivnePrijave,
  lokacijaZaSpasioca,
  spasi
}=require('../controllers/prijave-controller');

/* GET users listing. */
router.post('/', auth, novaPrijava);
router.get('/', auth, aktivnePrijave);
router.get('/zaSpasioca', auth, lokacijaZaSpasioca);
router.post('/spasi', auth, spasi);

module.exports = router;
