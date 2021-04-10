const pool = require('./connection');
const tabela='prijave';

const prijave = {
  insert: async function(prijava){
    let conn;
    try {
      conn = await pool.getConnection();
      const res = await conn.query("INSERT INTO "+tabela+" (id_korisnika,vreme,lokacija_prijave) VALUES (?,?,?)", [prijava.id_korisnika,new Date(),prijava.lokacija_prijave]);
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