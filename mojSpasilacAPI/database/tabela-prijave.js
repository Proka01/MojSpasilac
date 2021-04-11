const pool = require('./connection');
const tabela='prijave';

const prijave = {
  insert: async function(prijava){
    let conn;
    try {
      conn = await pool.getConnection();
      const res = await conn.query("INSERT INTO "+tabela+" (id_korisnika,vreme,lokacija_prijave_x, lokacija_prijave_y) VALUES (?,?,?,?)", [prijava.id_korisnika,new Date(),prijava.lokacija_prijave_x,prijava.lokacija_prijave_y]);
      console.log(res); 
      if(res.affectedRows==0)
        throw new Error('Nije uspelo upisivanje u bazu');
      conn.end();
      return res;
    } catch (err) {
      throw err;
    }
  },
  select: async function(){
    let conn;
    try {
      conn = await pool.getConnection();
      const res = await conn.query("SELECT * from "+tabela+" WHERE aktivna_prijava=1");
      //console.log(res);
      conn.end();
      return res;
    } catch (err) {
      throw err;
    }
  },
  prijavaZaVozilo(id_vozila){
    let conn;
    try {
      conn = await pool.getConnection();
      const res = await conn.query("SELECT * from vozila JOIN vozila_prijave USING(id_vozila) JOIN prijave USING(id_prijave) WHERE aktivna_prijava=1 AND id_vozila=? LIMIT 1",[id_vozila]);
      //console.log(res);
      conn.end();
      return res;
    } catch (err) {
      throw err;
    }
  },
  nadjiLokaciju(lista, id_korisnika){
    try{
      for(let i = 0; i < lista.length; i++)
      {
        if(lista[i].id_korisnika == id_korisnika) return lista[i];
      }
      console.log("prazan" + id_korisnika);
      return null;
    } catch (err) {
    throw err;
  }
  },
  update: async function(id_prijave){
    let conn;
    try {
      conn = await pool.getConnection();
      const res = await conn.query("UPDATE "+tabela+" SET aktivna_prijava=0 WHERE id_prijave=?", [id_prijave]);
      console.log(res);
      if(res.affectedRows==0)
        throw new Error('Nije uspelo upisivanje u bazu');
      conn.end();
      return res;
    } catch (err) {
      throw err;
    }
  }
}

module.exports=prijave;