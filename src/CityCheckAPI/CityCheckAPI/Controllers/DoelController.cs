using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using BussinessLayer.Methods;
using DataLayer;
using Microsoft.AspNetCore.Mvc;

namespace CityCheckAPI.Controllers
{
    [Route("api/citycheck")]
    [ApiController]
    public class DoelController : Controller
    {

        private readonly CityCheckContext context;

        private DoelMethod doelMethods;


        public DoelController(CityCheckContext context)
        {
            this.context = context;

            this.doelMethods = new DoelMethod(context);


        }



        //controllers----------

        //get all doellocs
        [HttpGet]
        [Route("allDoelLocs")]
        public IActionResult getAllLocs(string Naam, int? page, int? pageLength = 5, string direction = "asc")
        {

            List<DoelLocatie> doelen = doelMethods.getAllLocs(Naam, page,pageLength,direction);
            if (doelen != null)
                return Ok(doelen);
            else
                return NotFound();
        }



        //add new doelloc
        [HttpPost]
        [Route("addDoelLocs")]
        public IActionResult addLoc([FromBody] DoelLocatie newDoel)
        {

            var doelAdded = doelMethods.addLoc(newDoel);
            if (!doelAdded)
                return NotFound();
            else
                return Ok(newDoel);
        }



    }
}