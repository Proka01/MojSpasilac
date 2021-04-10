var express = require('express');
var router = express.Router();
var {auth}=require('../auth/jwt');
const {
  dodajVozilo,
  vratiVozila,
  upisiLok
}=require('../controllers/vozila-controller');

/* GET users listing. */
router.post('/', auth,  dodajVozilo);
router.get('/', auth,  vratiVozila);
router.post('/lokacija', auth, upisiLok);

module.exports = router;
