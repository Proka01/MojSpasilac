const pool = require('./connection');
const tabela='korisnici';

const korisnici={
  select: async function() {
    let conn;
    try {
      conn = await pool.getConnection();
      const res = await conn.query("SELECT * from "+tabela);
      //console.log(res); 
      conn.end();
      return res;
    } catch (err) {
      throw err;
    }
  },
  selectID: async function(id){
    let conn;
    try {
      conn = await pool.getConnection();
      const res = await conn.query("SELECT * from "+tabela+" where id_korisnika=?", [id]);
      //console.log(res); 
      conn.end();
      return res;
    } catch (err) {
      throw err;
    }
  },
  selectUsername: async function(username){
    let conn;
    try {
      conn = await pool.getConnection();
      const res = await conn.query("SELECT * from "+tabela+" where username=?", [username]);
      conn.end();
      return res[0];
    } catch (err) {
      throw err;
    }
  },
  insert: async function(username,password,id_tipa_korisnika){
    let conn;
    try {
      conn = await pool.getConnection();
      let c = await conn.query("SELECT COUNT(*) as cnt from "+tabela+" WHERE username=?", [username]);
      //console.log(c[0].cnt)
      if(c[0].cnt>0)
        throw new Error('Korisnik sa unetim imenom već postoji');
      const res = await conn.query("INSERT INTO "+tabela+" (username,password,id_tipa_korisnika) VALUES (?,?,?)", [username,password,id_tipa_korisnika]);
      //console.log(res); 
      if(res.affectedRows==0)
        throw new Error('Nije uspelo upisivanje u bazu');
      conn.end();
      return res;
    } catch (err) {
      throw err;
    }
  }
}

module.exports=korisnici;