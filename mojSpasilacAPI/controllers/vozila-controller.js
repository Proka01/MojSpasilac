const vozila = require('../database/tabela-vozila');

async function dodajVozilo(req,res){
  try{
    if(req.dekriptovan.id_korisnika != 3)
    {
        res.status(403).end();
        return;
    }
    //console.log(req.dekriptovan.id_korisnika,req.body);
    req.body.id_korisnika=req.dekriptovan.id_korisnika;
    const r=await vozila.insert(req.body);
    res.status(200).json(r);
  } catch(err){
    console.error(err);
    res.status(500).json(err);
  }}

module.exports = {
  dodajVozilo
};