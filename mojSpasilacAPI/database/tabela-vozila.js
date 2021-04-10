const pool = require('./connection');
const tabela='vozila';

const vozila = {
  insert: async function(vozilo){
    let conn;
    try {
      conn = await pool.getConnection();
      const res = await conn.query("INSERT INTO "+tabela+
      " (id_korisnika, kapacitet,lokacija_vozila_x,lokacija_vozila_y) VALUES (?,?,?,?)", [vozilo.id_korisnika,vozilo.kapacitet,vozilo.lokacija_vozila_x,vozilo.lokacija_vozila_y]);
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

module.exports=vozila;