/**
 * Sherlock Search Viewer component
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-22
 *
 * Description : Displays search results.
 **/

 import React from 'react';

 // NOTE: Yes, the innerHTML is set which is NOT good practice, but the text is stripped of html leaving
 // only html that's injected by the search api function (i.e wrapping text with the <strong> tag).

 /*eslint-disable react/no-danger */
 const SearchViewer = ({ searchResults, onSearchResultClicked }) => {
     if (searchResults.length > 0) {
         return (
             <div>
             {
                 searchResults.map((result, i) => {
                     return (
                         <div key={i}
                             onClick={ () => {
                                 onSearchResultClicked(result.path);
                             }}
                             className="file-browser-cell">
                                 <div className="file-browser-cell-path">
                                    { result.path.join('/') }
                                 </div>
                                 <div>
                                     <table className="file-browser-cell-frags">
                                         <tbody>
                                         {
                                             result.frags.map((frag, j) => {
                                                 return (
                                                     <tr onClick={e => {
                                                         e.stopPropagation();
                                                         onSearchResultClicked(result.path, parseInt(frag.line));
                                                     }} className="file-browser-cell-frag" key={j}>
                                                         <td className="file-browser-cell-line">
                                                              <div>{ frag.line }</div>
                                                         </td>
                                                         <td className="file-browser-cell-text">
                                                             <div dangerouslySetInnerHTML={{ __html: frag.frag }}></div>
                                                         </td>
                                                     </tr>
                                                 );
                                             })
                                         }
                                         </tbody>
                                    </table>
                               </div>
                         </div>
                     );
                 })
             }
             </div>
         );
     }

     return (
        <div className="file-browser-placeholder">No Results Found</div>
     );
 };

 export default SearchViewer;
