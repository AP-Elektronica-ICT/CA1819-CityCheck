using System.Collections.Generic;
using System.Linq;
using Microsoft.AspNetCore.Cors;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Model;


    [Route("api/citycheck")]
    [ApiController]
    public class CityCheckController : Controller
    {

    private readonly CityCheckContext context;


    public CityCheckController(CityCheckContext context)
    {
        this.context = context;
    }




}
