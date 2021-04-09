const korisnici=require('../database/tabela-korisnici');
const {generisiToken}=require('../auth/jwt');
const bcrypt = require('bcrypt');
const { JsonWebTokenError } = require('jsonwebtoken');
const saltRounds = 10;

async function prikazi(req, res) {
  try{
    const s = await korisnici.select();
    res.status(200).json(s);
  }
  catch(err){
    console.error(err);
    res.status(500).json(err);
  }
}

async function prikaziJedan(req, res) {
  try{
    const s = await korisnici.selectID(req.params.id);
    //console.log("S: "+s);
    res.status(200).json(s);
  }
  catch(err){
    console.error(err);
    res.status(500).json(err);
  }
}

async function signUp(req,res){

  try{
    const hash = await bcrypt.hash(req.body.password, saltRounds);
    const r = await korisnici.insert(req.body.username, hash);
    //console.log(r);
    res.status(200).json(r);
  } catch(err){
    console.error(err);
    if(err.message=='Korisnik sa unetim imenom već postoji')
      res.status(403).json(err);
    else
      res.status(500).json(err);
  }
}

async function logIn(req,res){

  try{
    const k = await korisnici.selectUsername(req.body.username);
    if(k==undefined)
      return res.status(403).json({err:'Pogrešno korisničko ime'});
    //console.log(req.body.password, k.password)
    const match = await bcrypt.compare(req.body.password, k.password);
    if(!match)
      return res.status(403).json({err:'Pogrešna lozinka'});

    const token=await generisiToken(k.id_korisnika,k.verifikovan);
    res.status(200).json({token:token});
  } catch(err){
    console.error(err);
    if(err.message=='Korisnik sa unetim imenom već postoji')
      res.status(403).json(err);
    else
      res.status(500).json(err);
  }
}


module.exports = {
  prikazi,
  prikaziJedan,
  signUp,
  logIn
};