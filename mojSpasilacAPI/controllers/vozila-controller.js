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
async function vratiVozila(req, res){
    try{
        if(req.dekriptovan.id_tipa_korisnika != 1){
            res.status(403).end();
            console.log("nije admin");
            return;
        }
        const s = await vozila.svaVozila();
        res.status(200).json(s);
    }
    catch(err){
        console.error(err);
        res.status(500).json(err);
      }
}
async function upisiLok(req, res){
  try{
      if(req.dekriptovan.id_tipa_korisnika != 3){
          res.status(403).end();
          console.log("nije dobrovoljac");
          return;
      }
      const s = await vozila.insertLok(req.dekriptovan.id_korisnika, req.body);
      res.status(200).json(s);
  }
  catch(err){
      console.error(err);
      res.status(500).json(err);
    }
}
module.exports = {
  dodajVozilo,
  vratiVozila,
  upisiLok
};