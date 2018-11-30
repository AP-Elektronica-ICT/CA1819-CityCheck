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



        //edit doelloc
        [HttpPut]
        [Route("editDoelLocs/{id}")]
        public IActionResult editLoc([FromBody] DoelLocatie newDoel, int id)
        {

            var doelEdited = doelMethods.editLoc(newDoel, id);
            if (!doelEdited)
                return NotFound();
            else
                return Ok(newDoel);
        }



        //delete doelloc
        [HttpDelete]
        [Route("delDoelLocs/{id}")]
        public IActionResult delLoc(int id)
        {

            var doelDeleted= doelMethods.delLoc(id);
            if (!doelDeleted)
                return NotFound();
            else
                return Ok("Deleted");
        }


        //add new question on location
        [HttpPost]
        [Route("addDoelLocsQuestion/{id}")]
        public IActionResult addQuest([FromBody] Vraag newVraag, int id)
        {
            //id is de id van de doellocatie

            var questAdded = doelMethods.addQuest(newVraag, id);
            if (!questAdded)
                return NotFound();
            else
                return Ok("Created");
        }



        //get all questions at 1 loc
        [HttpGet]
        [Route("allDoelLocsQuest/{id}")]
        public IActionResult getAllLocsQuest(int id)
        {

            List<Vraag> vragen = doelMethods.getAllLocsQuest(id);
            if (vragen != null)
                return Ok(vragen);
            else
                return NotFound();
        }






    }
}