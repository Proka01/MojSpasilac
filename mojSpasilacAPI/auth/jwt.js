const jwt = require("jsonwebtoken");

const generisiToken = async (id_korisnika,verifikovan) => {
  return await jwt.sign({id_korisnika:id_korisnika,verifikovan:verifikovan}, process.env.TOKEN_SECRET);
};

const proveriToken = async function (token){
  return await jwt.verify(token,process.env.TOKEN_SECRET);
};

const decrypt = async function(authorization){
  try{
    if(authorization==undefined)
      return null;
    const t=(authorization.split(" "))[1];
    console.log(t);
    const dekriptovan=await proveriToken(t);
    return dekriptovan;
  }catch(err){
    return null;  
  }
}

const auth = async function(req,res,next){
  try{
    console.log(req.headers.authorization);
    const t=(req.headers.authorization.split(" "))[1];
    console.log(t);
    const dekriptovan=await proveriToken(t);
    req.dekriptovan=dekriptovan;
    console.log(dekriptovan);
    next();
  }catch(err){
    console.error(err);
    res.status(403).json(err);
  }
}


module.exports={
  generisiToken,
  proveriToken,
  decrypt,
  auth};