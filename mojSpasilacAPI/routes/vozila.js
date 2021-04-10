var express = require('express');
var router = express.Router();
var {auth}=require('../auth/jwt');
const {
  dodajVozilo
}=require('../controllers/vozila-controller');

/* GET users listing. */
router.post('/', auth,  dodajVozilo);

module.exports = router;
