const osobe = require('../database/tabela-osobe');
const prijave=require('../database/tabela-prijave');
const vozila = require('../database/tabela-vozila');

async function novaPrijava(req,res){
  try{
    req.body.id_korisnika=req.dekriptovan.id_korisnika;
    //console.log(req.dekriptovan.id_korisnika,req.body);
    const r=await prijave.insert(req.body);
    const id_prijave=r.insertId;
    
    if(req.body.osobe.length>0)
      osobe.insert(id_prijave,req.body.osobe);
    res.status(200).json(r);
  } catch(err){
    console.error(err);
    res.status(500).json(err);
  }}
async function aktivnePrijave(req, res){
  try{
    if(req.dekriptovan.id_tipa_korisnika != 1){
        res.status(403).end();
        console.log("nije admin");
        return;
    }
    const s = await prijave.select();
    res.status(200).json(s);
}
catch(err){
    console.error(err);
    res.status(500).json(err);
  }
}
async function lokacijaZaSpasioca(req, res)
{
  try{
    if(req.dekriptovan.id_tipa_korisnika != 3){
        res.status(403).end();
        console.log("nije spasilac");
        return;
    }
    const t = await vozila.svaVozila();
    const s = await prijave.nadjiLokaciju(t,req.dekriptovan.id_korisnika);
    res.status(200).json(s);
}
catch(err){
    console.error(err);
    res.status(500).json(err);
  
}
}
async function spasi(req, res)
{
  if(req.dekriptovan.id_tipa_korisnika != 3){
    res.status(403).end();
    console.log("nije spasilac");
    return;
  }
  const s = await prijave.update(req.body.id_prijave);
  res.status(200).json(s);
}

module.exports = {
  novaPrijava,
  aktivnePrijave,
  lokacijaZaSpasioca,
  spasi
};