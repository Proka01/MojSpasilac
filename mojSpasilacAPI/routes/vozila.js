var express = require('express');
var router = express.Router();
var {auth}=require('../auth/jwt');
const {
  dodajVozilo,
  vratiVozila,
  upisiLok,
  dodeliPrijavu
}=require('../controllers/vozila-controller');

/* GET users listing. */
router.post('/', auth,  dodajVozilo);
router.get('/', auth,  vratiVozila);
router.post('/lokacija', auth, upisiLok);
router.post('/dodeli', auth, dodeliPrijavu);

module.exports = router;
