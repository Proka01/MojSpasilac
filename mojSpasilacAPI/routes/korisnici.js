var express = require('express');
var router = express.Router();
const { 
  signUp,
  logIn
}=require('../controllers/korisnici-controller');

/* GET users listing. */
router.post('/signup', signUp);
router.post('/login', logIn);

module.exports = router;
